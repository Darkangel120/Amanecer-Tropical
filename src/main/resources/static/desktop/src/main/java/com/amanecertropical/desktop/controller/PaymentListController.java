package com.amanecertropical.desktop.controller;

import com.amanecertropical.desktop.model.Payment;
import com.amanecertropical.desktop.util.SessionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class PaymentListController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(PaymentListController.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private ComboBox<String> methodFilter;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Button filterButton;
    @FXML private TableView<Payment> paymentTable;
    @FXML private TableColumn<Payment, Long> idColumn;
    @FXML private TableColumn<Payment, String> clientColumn;
    @FXML private TableColumn<Payment, String> reservationColumn;
    @FXML private TableColumn<Payment, String> amountColumn;
    @FXML private TableColumn<Payment, String> methodColumn;
    @FXML private TableColumn<Payment, String> referenceColumn;
    @FXML private TableColumn<Payment, String> dateColumn;
    @FXML private TableColumn<Payment, String> statusColumn;
    @FXML private TableColumn<Payment, Void> actionsColumn;
    @FXML private Button addButton;
    @FXML private Button refreshButton;
    @FXML private Label statusLabel;

    private ObservableList<Payment> payments = FXCollections.observableArrayList();
    private FilteredList<Payment> filteredPayments;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        setupFilters();
        loadPayments();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        clientColumn.setCellValueFactory(data -> {
            var reservation = data.getValue().getReservacion();
            if (reservation != null && reservation.getUsuario() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    reservation.getUsuario().getNombre()
                );
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });
        reservationColumn.setCellValueFactory(data -> {
            var reservation = data.getValue().getReservacion();
            return new javafx.beans.property.SimpleStringProperty(
                reservation != null ? "Res. " + reservation.getId() : "N/A"
            );
        });
        amountColumn.setCellValueFactory(data -> {
            var amount = data.getValue().getMonto();
            return new javafx.beans.property.SimpleStringProperty(
                amount != null ? "$" + String.format("%.2f", amount) : "N/A"
            );
        });
        methodColumn.setCellValueFactory(new PropertyValueFactory<>("metodoPago"));
        referenceColumn.setCellValueFactory(new PropertyValueFactory<>("referenciaPago"));
        dateColumn.setCellValueFactory(data -> {
            var date = data.getValue().getFechaPago();
            return new javafx.beans.property.SimpleStringProperty(
                date != null ? date.format(DATE_FORMATTER) : "N/A"
            );
        });
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("estadoPago"));

        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Editar");
            private final Button deleteButton = new Button("Eliminar");
            private final Button processButton = new Button("Procesar");
            private final HBox buttons = new HBox(5, editButton, processButton, deleteButton);

            {
                editButton.getStyleClass().addAll("form-button", "edit");
                editButton.setOnAction(event -> {
                    Payment payment = getTableView().getItems().get(getIndex());
                    handleEditPayment(payment);
                });

                processButton.getStyleClass().addAll("form-button", "success");
                processButton.setOnAction(event -> {
                    Payment payment = getTableView().getItems().get(getIndex());
                    handleProcessPayment(payment);
                });

                deleteButton.getStyleClass().addAll("form-button", "delete");
                deleteButton.setOnAction(event -> {
                    Payment payment = getTableView().getItems().get(getIndex());
                    handleDeletePayment(payment);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Payment payment = getTableView().getItems().get(getIndex());
                    processButton.setVisible("sin confirmacion".equals(payment.getEstadoPago()));
                    processButton.setManaged("sin confirmacion".equals(payment.getEstadoPago()));
                    setGraphic(buttons);
                }
            }
        });
    }

    private void setupFilters() {
        statusFilter.setItems(FXCollections.observableArrayList(
            "Todos", "sin confirmacion", "confirmado", "rechazado", "reembolsado"
        ));
        statusFilter.setValue("Todos");

        methodFilter.setItems(FXCollections.observableArrayList(
            "Todos", "tarjeta credito", "tarjeta debito", "transferencia", "pagomovil", "efectivo", "paypal"
        ));
        methodFilter.setValue("Todos");

        filteredPayments = new FilteredList<>(payments);
        paymentTable.setItems(filteredPayments);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });

        statusFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });

        methodFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });
    }

    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase();
        String selectedStatus = statusFilter.getValue();
        String selectedMethod = methodFilter.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        Predicate<Payment> searchPredicate = payment -> {
            if (searchText.isEmpty()) return true;

            var reservation = payment.getReservacion();
            if (reservation != null && reservation.getUsuario() != null) {
                if (reservation.getUsuario().getNombre().toLowerCase().contains(searchText)) {
                    return true;
                }
            }

            if (payment.getReferenciaPago() != null &&
                payment.getReferenciaPago().toLowerCase().contains(searchText)) {
                return true;
            }

            if (payment.getMetodoPago() != null &&
                payment.getMetodoPago().toLowerCase().contains(searchText)) {
                return true;
            }

            return false;
        };

        Predicate<Payment> statusPredicate = payment -> {
            if ("Todos".equals(selectedStatus)) return true;
            return selectedStatus.equals(payment.getEstadoPago());
        };

        Predicate<Payment> methodPredicate = payment -> {
            if ("Todos".equals(selectedMethod)) return true;
            return selectedMethod.equals(payment.getMetodoPago());
        };

        Predicate<Payment> datePredicate = payment -> {
            if (startDate == null && endDate == null) return true;

            LocalDateTime paymentDateTime = payment.getFechaPago();
            if (paymentDateTime == null) return false;

            LocalDate paymentDate = paymentDateTime.toLocalDate();
            boolean matchesStart = startDate == null || !paymentDate.isBefore(startDate);
            boolean matchesEnd = endDate == null || !paymentDate.isAfter(endDate);

            return matchesStart && matchesEnd;
        };

        filteredPayments.setPredicate(searchPredicate.and(statusPredicate).and(methodPredicate).and(datePredicate));
    }

    @FXML
    private void handleAddPayment() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PaymentFormView.fxml"));
            GridPane formView = loader.load();

            PaymentFormController controller = loader.getController();
            controller.setPayment(null);
            controller.setOnSaveCallback(() -> {
                loadPayments();
            });

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Nuevo Pago");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(addButton.getScene().getWindow());
            dialogStage.setScene(new Scene(formView));
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

            logger.info("Add payment dialog opened");
        } catch (IOException e) {
            logger.error("Error opening add payment dialog", e);
            showAlert("Error", "Error al abrir el formulario de pago: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditPayment(Payment payment) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PaymentFormView.fxml"));
            GridPane formView = loader.load();

            PaymentFormController controller = loader.getController();
            controller.setPayment(payment);
            controller.setOnSaveCallback(() -> {
                loadPayments();
            });

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Editar Pago");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(paymentTable.getScene().getWindow());
            dialogStage.setScene(new Scene(formView));
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

            logger.info("Edit payment dialog opened for payment: {}", payment.getId());
        } catch (IOException e) {
            logger.error("Error opening edit payment dialog", e);
            showAlert("Error", "Error al abrir el formulario de pago: " + e.getMessage());
        }
    }

    @FXML
    private void handleProcessPayment(Payment payment) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar pago");
        alert.setHeaderText("¿Confirmar el procesamiento de este pago?");
        alert.setContentText("Pago ID: " + payment.getId() +
                           "\nMonto: $" + String.format("%.2f", payment.getMonto()) +
                           "\nCliente: " + (payment.getReservacion() != null &&
                                          payment.getReservacion().getUsuario() != null ?
                                          payment.getReservacion().getUsuario().getNombre() : "N/A"));

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                processPayment(payment);
            }
        });
    }

    @FXML
    private void handleDeletePayment(Payment payment) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Está seguro de que desea eliminar este pago?");
        alert.setContentText("Pago ID: " + payment.getId() +
                           "\nMonto: $" + String.format("%.2f", payment.getMonto()));

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                deletePayment(payment);
            }
        });
    }

    @FXML
    private void handleRefresh() {
        loadPayments();
    }

    @FXML
    private void handleSearch() {
        applyFilters();
    }

    @FXML
    private void handleStatusFilter() {
        applyFilters();
    }

    @FXML
    private void handleMethodFilter() {
        applyFilters();
    }

    @FXML
    private void handleDateFilter() {
        applyFilters();
    }

    private void loadPayments() {
        statusLabel.setText("Cargando pagos...");
        statusLabel.getStyleClass().removeAll("error-label", "success-label");

        new Thread(() -> {
            try {
                
                var apiClient = SessionManager.getInstance().getApiClient();
                var request = new okhttp3.Request.Builder()
                    .url(apiClient.getBaseUrl() + "/payments")
                    .addHeader("Authorization", "Bearer " + SessionManager.getInstance().getAuthToken())
                    .build();

                try (var response = apiClient.getClient().newCall(request).execute()) {
                    final String responseBody;
                    if (response.body() != null) {
                        responseBody = response.body().string();
                    } else {
                        responseBody = null;
                    }

                    Platform.runLater(() -> {
                        if (response.isSuccessful() && responseBody != null) {
                            try {
                                Payment[] paymentsArray = apiClient.getGson().fromJson(responseBody, Payment[].class);
                                payments.clear();
                                payments.addAll(java.util.Arrays.asList(paymentsArray));
                                statusLabel.setText("Pagos cargados exitosamente (" + payments.size() + ")");
                                statusLabel.getStyleClass().add("success-label");
                                applyFilters();
                            } catch (Exception e) {
                                statusLabel.setText("Error al procesar respuesta: " + e.getMessage());
                                statusLabel.getStyleClass().add("error-label");
                                logger.error("Failed to parse payments response", e);
                            }
                        } else {
                            String errorMsg = "Error al cargar pagos";
                            if (responseBody != null) {
                                errorMsg += ": " + responseBody;
                            } else {
                                errorMsg += " (código: " + response.code() + ")";
                            }
                            statusLabel.setText(errorMsg);
                            statusLabel.getStyleClass().add("error-label");
                            logger.error("Failed to load payments: {}", errorMsg);
                        }
                    });
                }
            } catch (IOException e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Error de conexión: " + e.getMessage());
                    statusLabel.getStyleClass().add("error-label");
                    logger.error("Connection error while loading payments", e);
                });
            }
        }).start();
    }

    private void processPayment(Payment payment) {
        statusLabel.setText("Procesando pago...");
        statusLabel.getStyleClass().removeAll("error-label", "success-label");

        new Thread(() -> {
            try {
                payment.setEstadoPago("confirmado");
                var apiClient = SessionManager.getInstance().getApiClient();
                var response = apiClient.put("/payments/" + payment.getId(), payment, Payment.class);

                Platform.runLater(() -> {
                    if (response.isSuccess()) {
                        statusLabel.setText("Pago procesado exitosamente");
                        statusLabel.getStyleClass().add("success-label");
                        paymentTable.refresh();
                    } else {
                        statusLabel.setText("Error al procesar pago: " + response.getErrorMessage());
                        statusLabel.getStyleClass().add("error-label");
                        logger.error("Failed to process payment: {}", response.getErrorMessage());
                    }
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Error de conexión: " + e.getMessage());
                    statusLabel.getStyleClass().add("error-label");
                    logger.error("Connection error while processing payment", e);
                });
            }
        }).start();
    }

    private void deletePayment(Payment payment) {
        statusLabel.setText("Eliminando pago...");
        statusLabel.getStyleClass().removeAll("error-label", "success-label");

        new Thread(() -> {
            try {
                var apiClient = SessionManager.getInstance().getApiClient();
                var response = apiClient.delete("/payments/" + payment.getId());

                Platform.runLater(() -> {
                    if (response.isSuccess()) {
                        payments.remove(payment);
                        statusLabel.setText("Pago eliminado exitosamente");
                        statusLabel.getStyleClass().add("success-label");
                        applyFilters();
                    } else {
                        statusLabel.setText("Error al eliminar pago: " + response.getErrorMessage());
                        statusLabel.getStyleClass().add("error-label");
                        logger.error("Failed to delete payment: {}", response.getErrorMessage());
                    }
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Error de conexión: " + e.getMessage());
                    statusLabel.getStyleClass().add("error-label");
                    logger.error("Connection error while deleting payment", e);
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
}
