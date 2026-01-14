package com.amanecertropical.desktop.controller;

import com.amanecertropical.desktop.model.Reservation;
import com.amanecertropical.desktop.util.SessionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class ReservationListController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(ReservationListController.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Button filterButton;
    @FXML private TableView<Reservation> reservationTable;
    @FXML private TableColumn<Reservation, Long> idColumn;
    @FXML private TableColumn<Reservation, String> clientColumn;
    @FXML private TableColumn<Reservation, String> destinationColumn;
    @FXML private TableColumn<Reservation, String> startDateColumn;
    @FXML private TableColumn<Reservation, String> endDateColumn;
    @FXML private TableColumn<Reservation, Integer> peopleColumn;
    @FXML private TableColumn<Reservation, String> totalColumn;
    @FXML private TableColumn<Reservation, String> statusColumn;
    @FXML private TableColumn<Reservation, Void> actionsColumn;
    @FXML private Button addButton;
    @FXML private Button refreshButton;
    @FXML private Label statusLabel;

    private ObservableList<Reservation> reservations = FXCollections.observableArrayList();
    private FilteredList<Reservation> filteredReservations;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        setupFilters();
        loadReservations();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        clientColumn.setCellValueFactory(data -> {
            var user = data.getValue().getUsuario();
            return new javafx.beans.property.SimpleStringProperty(
                user != null ? user.getNombre() : "N/A"
            );
        });
        destinationColumn.setCellValueFactory(data -> {
            var reservation = data.getValue();
            String destination = "N/A";

            if (reservation.getPaquete() != null && reservation.getPaquete().getUbicacion() != null) {
                destination = reservation.getPaquete().getUbicacion();
            } else if (reservation.getHotel() != null && reservation.getHotel().getUbicacion() != null) {
                destination = reservation.getHotel().getUbicacion();
            }

            return new javafx.beans.property.SimpleStringProperty(destination);
        });
        startDateColumn.setCellValueFactory(data -> {
            var date = data.getValue().getFechaInicio();
            return new javafx.beans.property.SimpleStringProperty(
                date != null ? date.format(DATE_FORMATTER) : "N/A"
            );
        });
        endDateColumn.setCellValueFactory(data -> {
            var date = data.getValue().getFechaFin();
            return new javafx.beans.property.SimpleStringProperty(
                date != null ? date.format(DATE_FORMATTER) : "N/A"
            );
        });
        peopleColumn.setCellValueFactory(new PropertyValueFactory<>("numeroPersonas"));
        totalColumn.setCellValueFactory(data -> {
            var total = data.getValue().getPrecioTotal();
            return new javafx.beans.property.SimpleStringProperty(
                total != null ? "$" + total.toString() : "N/A"
            );
        });
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("estado"));

        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Editar");
            private final Button deleteButton = new Button("Eliminar");
            private final Button viewButton = new Button("Ver");
            private final HBox buttons = new HBox(5, viewButton, editButton, deleteButton);

            {
                editButton.getStyleClass().addAll("form-button", "edit");
                editButton.setOnAction(event -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    handleEditReservation(reservation);
                });

                deleteButton.getStyleClass().addAll("form-button", "delete");
                deleteButton.setOnAction(event -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    handleDeleteReservation(reservation);
                });

                viewButton.getStyleClass().addAll("form-button", "action-button-info");
                viewButton.setOnAction(event -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    handleViewReservation(reservation);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });
    }

    private void setupFilters() {
        statusFilter.setItems(FXCollections.observableArrayList(
            "Todos", "PENDIENTE", "CONFIRMADA", "CANCELADA", "COMPLETADA"
        ));
        statusFilter.setValue("Todos");

        filteredReservations = new FilteredList<>(reservations);
        reservationTable.setItems(filteredReservations);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });

        statusFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });
    }

    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase();
        String selectedStatus = statusFilter.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        Predicate<Reservation> searchPredicate = reservation -> {
            if (searchText.isEmpty()) return true;

            if (reservation.getUsuario() != null &&
                reservation.getUsuario().getNombre().toLowerCase().contains(searchText)) {
                return true;
            }

            String destination = "";
            if (reservation.getPaquete() != null && reservation.getPaquete().getUbicacion() != null) {
                destination = reservation.getPaquete().getUbicacion();
            } else if (reservation.getHotel() != null && reservation.getHotel().getUbicacion() != null) {
                destination = reservation.getHotel().getUbicacion();
            }

            return destination.toLowerCase().contains(searchText);
        };

        Predicate<Reservation> statusPredicate = reservation -> {
            if ("Todos".equals(selectedStatus)) return true;
            return selectedStatus.equals(reservation.getEstado());
        };

        Predicate<Reservation> datePredicate = reservation -> {
            if (startDate == null && endDate == null) return true;

            LocalDate resStart = reservation.getFechaInicio();
            LocalDate resEnd = reservation.getFechaFin();

            if (resStart == null || resEnd == null) return false;

            boolean matchesStart = startDate == null || !resEnd.isBefore(startDate);
            boolean matchesEnd = endDate == null || !resStart.isAfter(endDate);

            return matchesStart && matchesEnd;
        };

        filteredReservations.setPredicate(searchPredicate.and(statusPredicate).and(datePredicate));
    }

    @FXML
    private void handleAddReservation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ReservationFormView.fxml"));
            DialogPane formView = loader.load();
            formView.setPrefWidth(1000);
            formView.setPrefHeight(800);
            formView.setMinWidth(1000);
            formView.setMinHeight(800);
            formView.setMaxWidth(1000);
            formView.setMaxHeight(1000);

            ReservationFormController controller = loader.getController();
            controller.setReservation(null);
            controller.setOnSaveCallback(() -> {
                loadReservations();
            });

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Nueva Reservación");
            dialog.setDialogPane(formView);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            dialog.setResultConverter(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    controller.handleSave();
                    return ButtonType.OK;
                }
                return buttonType;
            });
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.initOwner(addButton.getScene().getWindow());
            dialog.setResizable(true);
            dialog.showAndWait();

            logger.info("Add reservation dialog opened");
        } catch (IOException e) {
            logger.error("Error opening add reservation dialog", e);
            showAlert("Error", "Error al abrir el formulario de reservación: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditReservation(Reservation reservation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ReservationFormView.fxml"));
            DialogPane formView = loader.load();
            formView.setPrefWidth(1000);
            formView.setPrefHeight(800);
            formView.setMinWidth(1000);
            formView.setMinHeight(800);
            formView.setMaxWidth(1000);
            formView.setMaxHeight(1000);

            ReservationFormController controller = loader.getController();
            controller.setReservation(reservation);
            controller.setOnSaveCallback(() -> {
                loadReservations();
            });

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Editar Reservación");
            dialog.setDialogPane(formView);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            dialog.setResultConverter(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    controller.handleSave();
                    return ButtonType.OK;
                }
                return buttonType;
            });
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.initOwner(reservationTable.getScene().getWindow());
            dialog.setResizable(true);
            dialog.showAndWait();

            logger.info("Edit reservation dialog opened for reservation: {}", reservation.getId());
        } catch (IOException e) {
            logger.error("Error opening edit reservation dialog", e);
            showAlert("Error", "Error al abrir el formulario de reservación: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewReservation(Reservation reservation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ReservationDetailsView.fxml"));
            DialogPane detailsView = loader.load();

            ReservationDetailsController controller = loader.getController();
            controller.setReservation(reservation);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Detalles de Reservación");
            dialog.setDialogPane(detailsView);
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.initOwner(reservationTable.getScene().getWindow());
            dialog.showAndWait();

            logger.info("View reservation dialog opened for reservation: {}", reservation.getId());
        } catch (IOException e) {
            logger.error("Error opening view reservation dialog", e);
            showAlert("Error", "Error al abrir los detalles de reservación: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteReservation(Reservation reservation) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Está seguro de que desea eliminar esta reservación?");
        alert.setContentText("Reservación ID: " + reservation.getId() +
                           "\nCliente: " + (reservation.getUsuario() != null ? reservation.getUsuario().getNombre() : "N/A"));

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                deleteReservation(reservation);
            }
        });
    }

    @FXML
    private void handleRefresh() {
        loadReservations();
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
    private void handleDateFilter() {
        applyFilters();
    }

    private void loadReservations() {
        statusLabel.setText("Cargando reservaciones...");
        statusLabel.getStyleClass().removeAll("error-label", "success-label");

        new Thread(() -> {
            try {
                var apiClient = SessionManager.getInstance().getApiClient();
                var request = new okhttp3.Request.Builder()
                    .url(apiClient.getBaseUrl() + "/reservations")
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
                                Reservation[] reservationsArray = apiClient.getGson().fromJson(responseBody, Reservation[].class);
                                reservations.clear();
                                reservations.addAll(java.util.Arrays.asList(reservationsArray));
                                statusLabel.setText("Reservaciones cargadas exitosamente (" + reservations.size() + ")");
                                statusLabel.getStyleClass().add("success-label");
                                applyFilters();
                            } catch (Exception e) {
                                statusLabel.setText("Error al procesar respuesta: " + e.getMessage());
                                statusLabel.getStyleClass().add("error-label");
                                logger.error("Failed to parse reservations response", e);
                            }
                        } else {
                            String errorMsg = "Error al cargar reservaciones";
                            if (responseBody != null) {
                                errorMsg += ": " + responseBody;
                            } else {
                                errorMsg += " (código: " + response.code() + ")";
                            }
                            statusLabel.setText(errorMsg);
                            statusLabel.getStyleClass().add("error-label");
                            logger.error("Failed to load reservations: {}", errorMsg);
                        }
                    });
                }
            } catch (IOException e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Error de conexión: " + e.getMessage());
                    statusLabel.getStyleClass().add("error-label");
                    logger.error("Connection error while loading reservations", e);
                });
            }
        }).start();
    }

    private void deleteReservation(Reservation reservation) {
        statusLabel.setText("Eliminando reservación...");
        statusLabel.getStyleClass().removeAll("error-label", "success-label");

        new Thread(() -> {
            try {
                var apiClient = SessionManager.getInstance().getApiClient();
                var response = apiClient.delete("/reservations/" + reservation.getId());

                Platform.runLater(() -> {
                    if (response.isSuccess()) {
                        reservations.remove(reservation);
                        statusLabel.setText("Reservación eliminada exitosamente");
                        statusLabel.getStyleClass().add("success-label");
                        applyFilters();
                    } else {
                        statusLabel.setText("Error al eliminar reservación: " + response.getErrorMessage());
                        statusLabel.getStyleClass().add("error-label");
                        logger.error("Failed to delete reservation: {}", response.getErrorMessage());
                    }
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Error de conexión: " + e.getMessage());
                    statusLabel.getStyleClass().add("error-label");
                    logger.error("Connection error while deleting reservation", e);
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
