package com.amanecertropical.desktop.controller;

import com.amanecertropical.desktop.model.User;
import com.amanecertropical.desktop.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UserFormController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(UserFormController.class);

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField cedulaField;
    @FXML private TextField telefonoField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private PasswordField passwordField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private Label titleLabel;

    private User user;
    private boolean isEditMode = false;
    private Runnable onSaveCallback;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupComboBoxes();
        setupValidation();
    }

    private void setupComboBoxes() {
        roleComboBox.setItems(FXCollections.observableArrayList(
            "USUARIO", "ADMIN", "EMPLEADO"
        ));
    }

    private void setupValidation() {
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !newValue.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                emailField.getStyleClass().add("error");
            } else {
                emailField.getStyleClass().remove("error");
            }
        });

        cedulaField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,10}")) {
                cedulaField.setText(oldValue);
            }
            if (newValue.length() == 10) {
                cedulaField.getStyleClass().remove("error");
            } else if (!newValue.isEmpty()) {
                cedulaField.getStyleClass().add("error");
            } else {
                cedulaField.getStyleClass().remove("error");
            }
        });

        telefonoField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !newValue.matches("^\\+?\\d{7,15}$")) {
                telefonoField.getStyleClass().add("error");
            } else {
                telefonoField.getStyleClass().remove("error");
            }
        });
    }

    public void setUser(User user) {
        this.user = user;
        this.isEditMode = user != null;

        if (isEditMode) {
            titleLabel.setText("Editar Usuario");
            populateFields();
        } else {
            titleLabel.setText("Nuevo Usuario");
            this.user = new User();
            roleComboBox.setValue("USUARIO");
        }
    }

    private void populateFields() {
        if (user != null) {
            nameField.setText(user.getNombre());
            emailField.setText(user.getCorreoElectronico());
            cedulaField.setText(user.getCedula());
            telefonoField.setText(user.getTelefono());
            roleComboBox.setValue(user.getRol());
        }
    }

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }

        try {
            user.setNombre(nameField.getText().trim());
            user.setCorreoElectronico(emailField.getText().trim());
            user.setCedula(cedulaField.getText().trim());
            user.setTelefono(telefonoField.getText().trim());
            user.setRol(roleComboBox.getValue());

            if (!passwordField.getText().isEmpty()) {
                user.setContrasena(passwordField.getText());
            }

            saveUser();
        } catch (Exception e) {
            showAlert("Error", "Error al procesar los datos: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty()) {
            showAlert("Error", "El nombre es requerido");
            return false;
        }

        if (emailField.getText().trim().isEmpty()) {
            showAlert("Error", "El correo electrónico es requerido");
            return false;
        }

        if (!emailField.getText().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            showAlert("Error", "El correo electrónico no tiene un formato válido");
            return false;
        }

        if (cedulaField.getText().trim().isEmpty()) {
            showAlert("Error", "La cédula es requerida");
            return false;
        }

        if (cedulaField.getText().length() != 10) {
            showAlert("Error", "La cédula debe tener 10 dígitos");
            return false;
        }

        if (telefonoField.getText().trim().isEmpty()) {
            showAlert("Error", "El teléfono es requerido");
            return false;
        }

        if (!telefonoField.getText().matches("^\\+?\\d{7,15}$")) {
            showAlert("Error", "El teléfono no tiene un formato válido");
            return false;
        }

        if (roleComboBox.getValue() == null) {
            showAlert("Error", "El rol es requerido");
            return false;
        }

        if (!isEditMode && passwordField.getText().isEmpty()) {
            showAlert("Error", "La contraseña es requerida para nuevos usuarios");
            return false;
        }

        if (!isEditMode && passwordField.getText().length() < 6) {
            showAlert("Error", "La contraseña debe tener al menos 6 caracteres");
            return false;
        }

        return true;
    }

    private void saveUser() {
        new Thread(() -> {
            try {
                var apiClient = SessionManager.getInstance().getApiClient();
                var response = isEditMode ?
                    apiClient.put("/users/" + user.getId(), user, User.class) :
                    apiClient.post("/users", user, User.class);

                javafx.application.Platform.runLater(() -> {
                    if (response.isSuccess()) {
                        if (onSaveCallback != null) {
                            onSaveCallback.run();
                        }
                        closeDialog();
                    } else {
                        showAlert("Error", "Error al guardar usuario: " + response.getErrorMessage());
                    }
                });
            } catch (IOException e) {
                logger.error("Error saving user", e);
                javafx.application.Platform.runLater(() -> {
                    showAlert("Error", "Error de conexión: " + e.getMessage());
                });
            }
        }).start();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeDialog() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}
