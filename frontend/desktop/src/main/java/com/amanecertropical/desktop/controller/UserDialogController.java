package com.amanecertropical.desktop.controller;

import com.amanecertropical.desktop.models.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class UserDialogController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> roleComboBox;

    private User user;

    @FXML
    public void initialize() {
        roleComboBox.setItems(FXCollections.observableArrayList("USER", "ADMIN"));
        roleComboBox.setValue("USER");
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            nameField.setText(user.getName());
            emailField.setText(user.getEmail());
            passwordField.setText(user.getPassword());
            roleComboBox.setValue(user.getRole().toString());
        }
    }

    public User getUser() {
        if (user == null) {
            user = new User();
        }
        user.setName(nameField.getText());
        user.setEmail(emailField.getText());
        user.setPassword(passwordField.getText());
        user.setRole(User.UserRole.valueOf(roleComboBox.getValue()));
        return user;
    }
}
