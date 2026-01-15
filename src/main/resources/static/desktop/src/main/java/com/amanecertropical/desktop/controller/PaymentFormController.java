
package com.amanecertropical.desktop.controller;

import com.amanecertropical.desktop.model.Payment;
import com.amanecertropical.desktop.model.Reservation;
import com.amanecertropical.desktop.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class PaymentFormController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(PaymentFormController.class);

    @FXML private TextField amountField;
    @FXML private ComboBox<String> methodComboBox;
    @FXML private TextField referenceField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private ComboBox<Reservation> reservationComboBox;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private Label titleLabel;

    private Payment payment;
    private boolean isEditMode = false;
    private Runnable onSaveCallback;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupComboBoxes();
        setupValidation();
    }

    private void setupComboBoxes() {
        methodComboBox.setItems(FXCollections.observableArrayList(
            "tarjeta_credito", "tarjeta_debito", "transferencia", "efectivo", "paypal"
        ));

        statusComboBox.setItems(FXCollections.observableArrayList(
            "sin confirmacion", "confirmado", "rechazado", "reembolsado"
        ));

        loadReservations();
    }

    private void setupValidation() {
        amountField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                amountField.setText(oldValue);
            }
        });
    }

    private void loadReservations() {
        new Thread(() -> {
            try {
                var apiClient = SessionManager.getInstance().getApiClient();
                var response = apiClient.get("/reservations", Reservation[].class);

                if (response.isSuccess()) {
                    Reservation[] reservationsArray = response.getData();
                    if (reservationsArray != null) {
                        javafx.application.Platform.runLater(() -> {
                            reservationComboBox.setItems(FXCollections.observableArrayList(reservationsArray));
                            reservationComboBox.setConverter(new javafx.util.StringConverter<Reservation>() {
                                @Override
                                public String toString(Reservation reservation) {
                                    if (reservation == null) return "";
                                    String clientName = reservation.getUsuario() != null ? reservation.getUsuario().getNombre() : "N/A";
                                    return "Res. " + reservation.getId() + " - " + clientName;
                                }

                                @Override
                                public Reservation fromString(String string) {
                                    return null;
                                }
                            });
                        });
                    } else {
                        javafx.application.Platform.runLater(() -> {
                            reservationComboBox.setItems(FXCollections.emptyObservableList());
                        });
                    }
                } else {
                    logger.error("Error loading reservations: {}", response.getErrorMessage());
                    javafx.application.Platform.runLater(() -> {
                        showAlert("Error", "Error al cargar reservaciones: " + response.getErrorMessage());
                    });
                }
            } catch (IOException e) {
                logger.error("Error loading reservations", e);
                javafx.application.Platform.runLater(() -> {
                    showAlert("Error", "Error al cargar reservaciones: " + e.getMessage());
                });
            }
        }).start();
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
        this.isEditMode = payment != null;

        if (isEditMode) {
            titleLabel.setText("Editar Pago");
            populateFields();
        } else {
            titleLabel.setText("Nuevo Pago");
            payment = new Payment();
            statusComboBox.setValue("sin confirmacion");
        }
    }

    private void populateFields() {
        if (payment != null) {
            amountField.setText(payment.getMonto() != null ? payment.getMonto().toString() : "");
            methodComboBox.setValue(payment.getMetodoPago());
            referenceField.setText(payment.getReferenciaPago());
            statusComboBox.setValue(payment.getEstadoPago());

            if (payment.getReservacion() != null) {
                for (Reservation res : reservationComboBox.getItems()) {
                    if (res.getId().equals(payment.getReservacion().getId())) {
                        reservationComboBox.setValue(res);
                        break;
                    }
                }
            }
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
            payment.setMonto(BigDecimal.valueOf(Double.parseDouble(amountField.getText())));
            payment.setMetodoPago(methodComboBox.getValue());
            payment.setReferenciaPago(referenceField.getText());
            payment.setEstadoPago(statusComboBox.getValue());
            payment.setReservacion(reservationComboBox.getValue());

            if (!isEditMode) {
                payment.setFechaPago(LocalDateTime.now());
            }

            savePayment();
        } catch (NumberFormatException e) {
            showAlert("Error", "Monto inválido");
        }
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private boolean validateForm() {
        if (amountField.getText().isEmpty()) {
            showAlert("Error", "El monto es requerido");
            return false;
        }

        if (methodComboBox.getValue() == null) {
            showAlert("Error", "El método de pago es requerido");
            return false;
        }

        if (referenceField.getText().isEmpty()) {
            showAlert("Error", "La referencia es requerida");
            return false;
        }

        if (statusComboBox.getValue() == null) {
            showAlert("Error", "El estado es requerido");
            return false;
        }

        if (reservationComboBox.getValue() == null) {
            showAlert("Error", "La reservación es requerida");
            return false;
        }

        return true;
    }

    private void savePayment() {
        new Thread(() -> {
            try {
                var apiClient = SessionManager.getInstance().getApiClient();
                var response = isEditMode ?
                    apiClient.put("/payments/" + payment.getId(), payment, Payment.class) :
                    apiClient.post("/payments", payment, Payment.class);

                javafx.application.Platform.runLater(() -> {
                    if (response.isSuccess()) {
                        if (onSaveCallback != null) {
                            onSaveCallback.run();
                        }
                        closeDialog();
                    } else {
                        showAlert("Error", "Error al guardar pago: " + response.getErrorMessage());
                    }
                });
            } catch (IOException e) {
                logger.error("Error saving payment", e);
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
