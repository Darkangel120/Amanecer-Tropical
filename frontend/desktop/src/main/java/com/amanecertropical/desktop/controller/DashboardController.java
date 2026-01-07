package com.amanecertropical.desktop.controller;

import com.amanecertropical.desktop.models.*;
import com.amanecertropical.desktop.service.ApiService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.List;

public class DashboardController {
    // FXML injected fields
    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, String> userNameColumn;
    @FXML
    private TableColumn<User, String> userEmailColumn;

    @FXML
    private TableView<Destination> destinationTable;
    @FXML
    private TableColumn<Destination, String> destinationNameColumn;
    @FXML
    private TableColumn<Destination, String> destinationDescriptionColumn;

    @FXML
    private TableView<Flight> flightTable;
    @FXML
    private TableColumn<Flight, String> flightNumberColumn;
    @FXML
    private TableColumn<Flight, String> flightOriginColumn;
    @FXML
    private TableColumn<Flight, String> flightDestinationColumn;

    @FXML
    private TableView<Hotel> hotelTable;
    @FXML
    private TableColumn<Hotel, String> hotelNameColumn;
    @FXML
    private TableColumn<Hotel, String> hotelLocationColumn;

    @FXML
    private TableView<Reservation> reservationTable;
    @FXML
    private TableColumn<Reservation, String> reservationUserColumn;
    @FXML
    private TableColumn<Reservation, String> reservationDetailsColumn;

    private ApiService apiService = new ApiService();

    @FXML
    public void initialize() {
        // Initialize table columns
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        userEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        destinationNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        destinationDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        flightNumberColumn.setCellValueFactory(new PropertyValueFactory<>("flightNumber"));
        flightOriginColumn.setCellValueFactory(new PropertyValueFactory<>("origin"));
        flightDestinationColumn.setCellValueFactory(new PropertyValueFactory<>("destination"));

        hotelNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        hotelLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));

        reservationUserColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        reservationDetailsColumn.setCellValueFactory(new PropertyValueFactory<>("details"));

        // Load data
        loadData();
    }

    private void loadData() {
        Platform.runLater(() -> {
            try {
                // Load users
                List<User> users = apiService.getUsers();
                userTable.setItems(FXCollections.observableArrayList(users));

                // Load destinations
                List<Destination> destinations = apiService.getDestinations();
                destinationTable.setItems(FXCollections.observableArrayList(destinations));

                // Load flights
                List<Flight> flights = apiService.getFlights();
                flightTable.setItems(FXCollections.observableArrayList(flights));

                // Load hotels
                List<Hotel> hotels = apiService.getHotels();
                hotelTable.setItems(FXCollections.observableArrayList(hotels));

                // Load reservations
                List<Reservation> reservations = apiService.getReservations();
                reservationTable.setItems(FXCollections.observableArrayList(reservations));

            } catch (IOException e) {
                showError("Failed to load data: " + e.getMessage());
            }
        });
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleLogout() {
        try {
            // Clear auth token
            apiService.setAuthToken(null);
            com.amanecertropical.desktop.MainApp.showLoginScreen();
        } catch (Exception e) {
            showError("Error al cerrar sesión: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddUser() {
        showInfo("Funcionalidad no implementada", "Agregar usuario");
    }

    @FXML
    private void handleEditUser() {
        showInfo("Funcionalidad no implementada", "Editar usuario");
    }

    @FXML
    private void handleDeleteUser() {
        showInfo("Funcionalidad no implementada", "Eliminar usuario");
    }

    @FXML
    private void handleAddDestination() {
        showInfo("Funcionalidad no implementada", "Agregar destino");
    }

    @FXML
    private void handleEditDestination() {
        showInfo("Funcionalidad no implementada", "Editar destino");
    }

    @FXML
    private void handleDeleteDestination() {
        showInfo("Funcionalidad no implementada", "Eliminar destino");
    }

    @FXML
    private void handleAddFlight() {
        showInfo("Funcionalidad no implementada", "Agregar vuelo");
    }

    @FXML
    private void handleEditFlight() {
        showInfo("Funcionalidad no implementada", "Editar vuelo");
    }

    @FXML
    private void handleDeleteFlight() {
        showInfo("Funcionalidad no implementada", "Eliminar vuelo");
    }

    @FXML
    private void handleAddHotel() {
        showInfo("Funcionalidad no implementada", "Agregar hotel");
    }

    @FXML
    private void handleEditHotel() {
        showInfo("Funcionalidad no implementada", "Editar hotel");
    }

    @FXML
    private void handleDeleteHotel() {
        showInfo("Funcionalidad no implementada", "Eliminar hotel");
    }

    @FXML
    private void handleAddReservation() {
        showInfo("Funcionalidad no implementada", "Agregar reservación");
    }

    @FXML
    private void handleEditReservation() {
        showInfo("Funcionalidad no implementada", "Editar reservación");
    }

    @FXML
    private void handleDeleteReservation() {
        showInfo("Funcionalidad no implementada", "Eliminar reservación");
    }

    private void showInfo(String message, String title) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
