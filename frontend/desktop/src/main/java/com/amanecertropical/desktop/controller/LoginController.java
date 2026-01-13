package com.amanecertropical.desktop.controller;

import com.amanecertropical.desktop.MainApp;
import com.amanecertropical.desktop.api.ApiClient;
import com.amanecertropical.desktop.model.User;
import com.amanecertropical.desktop.util.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;

    private ApiClient apiClient;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        apiClient = new ApiClient();
        setupValidation();
    }

    private void setupValidation() {
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            clearStatus();
        });

        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            clearStatus();
        });
    }

    @FXML
    private void handleLogin() {
        String username = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Por favor ingrese usuario y contraseña");
            return;
        }

        loginButton.setDisable(true);
        errorLabel.setText("Iniciando sesión...");
        errorLabel.getStyleClass().removeAll("error-label", "success-label");

        new Thread(() -> {
            try {
                var response = apiClient.login(username, password);

                Platform.runLater(() -> {
                    if (response.isSuccess()) {
                        logger.info("Login successful for user: {}", username);
                        errorLabel.setText("Inicio de sesión exitoso");
                        errorLabel.getStyleClass().add("success-label");

                        Map<String, Object> responseData = response.getData();
                        if (responseData != null) {
                            String token = (String) responseData.get("token");
                            @SuppressWarnings("unchecked")
                            Map<String, Object> userData = (Map<String, Object>) responseData.get("user");

                            if (token != null && userData != null) {
                                SessionManager.getInstance().setAuthToken(token);

                                User user = new User();
                                if (userData.get("id") != null) user.setId(((Double) userData.get("id")).longValue());
                                user.setNombre((String) userData.get("nombre"));
                                user.setCorreoElectronico((String) userData.get("correoElectronico"));
                                user.setCedula((String) userData.get("cedula"));
                                user.setRol((String) userData.get("rol"));

                                SessionManager.getInstance().setCurrentUser(user);

                                try {
                                    MainApp.showDashboard();
                                } catch (IOException e) {
                                    logger.error("Failed to show dashboard", e);
                                    showError("Error al cargar el dashboard");
                                }
                            } else {
                                showError("Respuesta del servidor inválida");
                            }
                        } else {
                            showError("Respuesta del servidor inválida");
                        }
                    } else {
                        logger.warn("Login failed for user: {}", username);
                        showError("Credenciales inválidas");
                    }
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    logger.error("Login connection error", e);
                    showError("Error de conexión. Verifique su conexión a internet.");
                });
            } finally {
                Platform.runLater(() -> {
                    loginButton.setDisable(false);
                });
            }
        }).start();
    }

    @FXML
    private void handleKeyPressed(javafx.scene.input.KeyEvent event) {
        if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
            handleLogin();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.getStyleClass().removeAll("success-label");
        errorLabel.getStyleClass().add("error-label");
    }

    private void clearStatus() {
        errorLabel.setText("");
        errorLabel.getStyleClass().removeAll("error-label", "success-label");
    }
}
