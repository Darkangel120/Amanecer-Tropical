package com.amanecertropical.desktop.controller;

import com.amanecertropical.desktop.service.ApiService;
import com.amanecertropical.desktop.MainApp;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    private final ApiService apiService = new ApiService();

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorLabel;

    @FXML
    private void handleLogin() {
        String email = usernameField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Por favor, ingrese email y contraseña.");
            errorLabel.setVisible(true);
            return;
        }

        loginButton.setDisable(true);
        errorLabel.setVisible(false);

        // Run login in background thread to avoid blocking UI
        new Thread(() -> {
            try {
                String token = apiService.login(email, password);
                apiService.setAuthToken(token);

                Platform.runLater(() -> {
                    try {
                        MainApp.showDashboard();
                    } catch (Exception e) {
                        e.printStackTrace();
                        errorLabel.setText("Error al cargar el dashboard.");
                        errorLabel.setVisible(true);
                        loginButton.setDisable(false);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    errorLabel.setText("Credenciales inválidas o error de conexión.");
                    errorLabel.setVisible(true);
                    loginButton.setDisable(false);
                });
            }
        }).start();
    }
}
