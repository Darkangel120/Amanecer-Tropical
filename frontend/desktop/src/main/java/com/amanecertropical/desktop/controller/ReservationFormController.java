package com.amanecertropical.desktop.controller;

import com.amanecertropical.desktop.model.Reservation;
import com.amanecertropical.desktop.model.User;
import com.amanecertropical.desktop.model.Package;
import com.amanecertropical.desktop.model.Flight;
import com.amanecertropical.desktop.model.Hotel;
import com.amanecertropical.desktop.model.Vehicle;
import com.amanecertropical.desktop.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ReservationFormController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(ReservationFormController.class);

    @FXML private ComboBox<User> userComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField peopleCountField;
    @FXML private TextField totalPriceField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private ComboBox<String> serviceTypeComboBox;
    @FXML private TextArea notesArea;
    @FXML private Label titleLabel;
    @FXML private VBox servicesContainer;
    @FXML private Button addServiceButton;

    private Reservation reservation;
    private boolean isEditMode = false;
    private Runnable onSaveCallback;

    // Data storage
    private List<Package> packages = new ArrayList<>();
    private List<Flight> flights = new ArrayList<>();
    private List<Hotel> hotels = new ArrayList<>();
    private List<Vehicle> vehicles = new ArrayList<>();

    // Selected services
    private List<Package> selectedPackages = new ArrayList<>();
    private List<Flight> selectedFlights = new ArrayList<>();
    private List<Hotel> selectedHotels = new ArrayList<>();
    private List<Vehicle> selectedVehicles = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupComboBoxes();
        setupValidation();
    }

    private void setupComboBoxes() {
        statusComboBox.setItems(FXCollections.observableArrayList(
            "PENDIENTE", "CONFIRMADA", "CANCELADA", "COMPLETADA"
        ));

        serviceTypeComboBox.setItems(FXCollections.observableArrayList(
            "Paquete", "Vuelo", "Hotel", "Vehículo"
        ));

        loadUsers();
        loadPackages();
        loadFlights();
        loadHotels();
        loadVehicles();
    }

    private void setupValidation() {
        peopleCountField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                peopleCountField.setText(oldValue);
            }
        });

        totalPriceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                totalPriceField.setText(oldValue);
            }
        });
    }

    private void loadUsers() {
        new Thread(() -> {
            try {
                var apiClient = SessionManager.getInstance().getApiClient();
                var request = new okhttp3.Request.Builder()
                    .url(apiClient.getBaseUrl() + "/users")
                    .addHeader("Authorization", "Bearer " + SessionManager.getInstance().getAuthToken())
                    .build();

                try (var response = apiClient.getClient().newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonResponse = response.body().string();
                        User[] usersArray = apiClient.getGson().fromJson(jsonResponse, User[].class);

                        javafx.application.Platform.runLater(() -> {
                            userComboBox.setItems(FXCollections.observableArrayList(usersArray));
                            userComboBox.setConverter(new javafx.util.StringConverter<User>() {
                                @Override
                                public String toString(User user) {
                                    return user != null ? user.getNombre() : "";
                                }

                                @Override
                                public User fromString(String string) {
                                    return null;
                                }
                            });
                        });
                    }
                }
            } catch (IOException e) {
                logger.error("Error loading users", e);
                javafx.application.Platform.runLater(() -> {
                    showAlert("Error", "Error al cargar usuarios: " + e.getMessage());
                });
            }
        }).start();
    }

    private void loadPackages() {
        new Thread(() -> {
            try {
                var apiClient = SessionManager.getInstance().getApiClient();
                var request = new okhttp3.Request.Builder()
                    .url(apiClient.getBaseUrl() + "/packages")
                    .addHeader("Authorization", "Bearer " + SessionManager.getInstance().getAuthToken())
                    .build();

                try (var response = apiClient.getClient().newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonResponse = response.body().string();
                        Package[] packagesArray = apiClient.getGson().fromJson(jsonResponse, Package[].class);

                        javafx.application.Platform.runLater(() -> {
                            packages.clear();
                            packages.addAll(Arrays.asList(packagesArray));
                        });
                    }
                }
            } catch (IOException e) {
                logger.error("Error loading packages", e);
                javafx.application.Platform.runLater(() -> {
                    showAlert("Error", "Error al cargar paquetes: " + e.getMessage());
                });
            }
        }).start();
    }

    private void loadFlights() {
        new Thread(() -> {
            try {
                var apiClient = SessionManager.getInstance().getApiClient();
                var request = new okhttp3.Request.Builder()
                    .url(apiClient.getBaseUrl() + "/flights")
                    .addHeader("Authorization", "Bearer " + SessionManager.getInstance().getAuthToken())
                    .build();

                try (var response = apiClient.getClient().newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonResponse = response.body().string();
                        Flight[] flightsArray = apiClient.getGson().fromJson(jsonResponse, Flight[].class);

                        javafx.application.Platform.runLater(() -> {
                            flights.clear();
                            flights.addAll(Arrays.asList(flightsArray));
                        });
                    }
                }
            } catch (IOException e) {
                logger.error("Error loading flights", e);
                javafx.application.Platform.runLater(() -> {
                    showAlert("Error", "Error al cargar vuelos: " + e.getMessage());
                });
            }
        }).start();
    }

    private void loadHotels() {
        new Thread(() -> {
            try {
                var apiClient = SessionManager.getInstance().getApiClient();
                var request = new okhttp3.Request.Builder()
                    .url(apiClient.getBaseUrl() + "/hotels")
                    .addHeader("Authorization", "Bearer " + SessionManager.getInstance().getAuthToken())
                    .build();

                try (var response = apiClient.getClient().newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonResponse = response.body().string();
                        Hotel[] hotelsArray = apiClient.getGson().fromJson(jsonResponse, Hotel[].class);

                        javafx.application.Platform.runLater(() -> {
                            hotels.clear();
                            hotels.addAll(Arrays.asList(hotelsArray));
                        });
                    }
                }
            } catch (IOException e) {
                logger.error("Error loading hotels", e);
                javafx.application.Platform.runLater(() -> {
                    showAlert("Error", "Error al cargar hoteles: " + e.getMessage());
                });
            }
        }).start();
    }

    private void loadVehicles() {
        new Thread(() -> {
            try {
                var apiClient = SessionManager.getInstance().getApiClient();
                var request = new okhttp3.Request.Builder()
                    .url(apiClient.getBaseUrl() + "/vehicles")
                    .addHeader("Authorization", "Bearer " + SessionManager.getInstance().getAuthToken())
                    .build();

                try (var response = apiClient.getClient().newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonResponse = response.body().string();
                        Vehicle[] vehiclesArray = apiClient.getGson().fromJson(jsonResponse, Vehicle[].class);

                        javafx.application.Platform.runLater(() -> {
                            vehicles.clear();
                            Collections.addAll(vehicles, vehiclesArray);
                        });
                    }
                }
            } catch (IOException e) {
                logger.error("Error loading vehicles", e);
                javafx.application.Platform.runLater(() -> {
                    showAlert("Error", "Error al cargar vehículos: " + e.getMessage());
                });
            }
        }).start();
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
        this.isEditMode = reservation != null;

        if (isEditMode) {
            titleLabel.setText("Editar Reservación");
            populateFields();
        } else {
            titleLabel.setText("Nueva Reservación");
            this.reservation = new Reservation();
            statusComboBox.setValue("PENDIENTE");
        }
    }

    private void populateFields() {
        if (reservation != null) {
            if (reservation.getUsuario() != null) {
                for (User user : userComboBox.getItems()) {
                    if (user.getId().equals(reservation.getUsuario().getId())) {
                        userComboBox.setValue(user);
                        break;
                    }
                }
            }

            // Populate dynamic services
            if (reservation.getPaquete() != null) {
                selectedPackages.add(reservation.getPaquete());
                addServiceRow("Paquete", reservation.getPaquete());
            }

            if (reservation.getVuelo() != null) {
                selectedFlights.add(reservation.getVuelo());
                addServiceRow("Vuelo", reservation.getVuelo());
            }

            if (reservation.getHotel() != null) {
                selectedHotels.add(reservation.getHotel());
                addServiceRow("Hotel", reservation.getHotel());
            }

            if (reservation.getVehiculo() != null) {
                selectedVehicles.add(reservation.getVehiculo());
                addServiceRow("Vehículo", reservation.getVehiculo());
            }

            startDatePicker.setValue(reservation.getFechaInicio());
            endDatePicker.setValue(reservation.getFechaFin());
            peopleCountField.setText(reservation.getNumeroPersonas() != null ? reservation.getNumeroPersonas().toString() : "");
            totalPriceField.setText(reservation.getPrecioTotal() != null ? reservation.getPrecioTotal().toString() : "");
            statusComboBox.setValue(reservation.getEstado());
            notesArea.setText(reservation.getNotas());
        }
    }

    private void addServiceRow(String type, Object service) {
        HBox serviceRow = new HBox(10);
        serviceRow.setPadding(new Insets(5));

        Label typeLabel = new Label(type + ":");
        final ComboBox[] serviceComboBox = new ComboBox[1];

        switch (type) {
            case "Paquete":
                serviceComboBox[0] = new ComboBox<>();
                serviceComboBox[0].setItems(FXCollections.observableArrayList(packages));
                serviceComboBox[0].setConverter(new javafx.util.StringConverter<Package>() {
                    @Override
                    public String toString(Package pkg) {
                        return pkg != null ? pkg.getNombre() : "";
                    }
                    @Override
                    public Package fromString(String string) {
                        return null;
                    }
                });
                serviceComboBox[0].setValue((Package) service);
                break;
            case "Vuelo":
                serviceComboBox[0] = new ComboBox<>();
                serviceComboBox[0].setItems(FXCollections.observableArrayList(flights));
                serviceComboBox[0].setConverter(new javafx.util.StringConverter<Flight>() {
                    @Override
                    public String toString(Flight flight) {
                        return flight != null ? flight.getOrigen() + " - " + flight.getDestino() : "";
                    }
                    @Override
                    public Flight fromString(String string) {
                        return null;
                    }
                });
                serviceComboBox[0].setValue((Flight) service);
                break;
            case "Hotel":
                serviceComboBox[0] = new ComboBox<>();
                serviceComboBox[0].setItems(FXCollections.observableArrayList(hotels));
                serviceComboBox[0].setConverter(new javafx.util.StringConverter<Hotel>() {
                    @Override
                    public String toString(Hotel hotel) {
                        return hotel != null ? hotel.getNombre() : "";
                    }
                    @Override
                    public Hotel fromString(String string) {
                        return null;
                    }
                });
                serviceComboBox[0].setValue((Hotel) service);
                break;
            case "Vehículo":
                serviceComboBox[0] = new ComboBox<>();
                serviceComboBox[0].setItems(FXCollections.observableArrayList(vehicles));
                serviceComboBox[0].setConverter(new javafx.util.StringConverter<Vehicle>() {
                    @Override
                    public String toString(Vehicle vehicle) {
                        return vehicle != null ? vehicle.getModelo() : "";
                    }
                    @Override
                    public Vehicle fromString(String string) {
                        return null;
                    }
                });
                serviceComboBox[0].setValue((Vehicle) service);
                break;
        }

        Button removeButton = new Button("Eliminar");
        removeButton.setOnAction(e -> {
            servicesContainer.getChildren().remove(serviceRow);
            // Remove from selected lists
            if (serviceComboBox[0].getValue() instanceof Package) {
                selectedPackages.remove((Package) serviceComboBox[0].getValue());
            } else if (serviceComboBox[0].getValue() instanceof Flight) {
                selectedFlights.remove((Flight) serviceComboBox[0].getValue());
            } else if (serviceComboBox[0].getValue() instanceof Hotel) {
                selectedHotels.remove((Hotel) serviceComboBox[0].getValue());
            } else if (serviceComboBox[0].getValue() instanceof Vehicle) {
                selectedVehicles.remove((Vehicle) serviceComboBox[0].getValue());
            }
            calculateTotalPrice();
        });

        serviceRow.getChildren().addAll(typeLabel, serviceComboBox[0], removeButton);
        servicesContainer.getChildren().add(serviceRow);
    }

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    @FXML
    public void handleSave() {
        if (!validateForm()) {
            return;
        }

        try {
            reservation.setUsuario(userComboBox.getValue());
            reservation.setUsuarioId(userComboBox.getValue().getId());
            reservation.setPaquete(selectedPackages.isEmpty() ? null : selectedPackages.get(0));
            if (!selectedPackages.isEmpty()) {
                reservation.setPaqueteId(selectedPackages.get(0).getId());
            }
            reservation.setVuelo(selectedFlights.isEmpty() ? null : selectedFlights.get(0));
            if (!selectedFlights.isEmpty()) {
                reservation.setVueloId(selectedFlights.get(0).getId());
            }
            reservation.setHotel(selectedHotels.isEmpty() ? null : selectedHotels.get(0));
            if (!selectedHotels.isEmpty()) {
                reservation.setHotelId(selectedHotels.get(0).getId());
            }
            reservation.setVehiculo(selectedVehicles.isEmpty() ? null : selectedVehicles.get(0));
            if (!selectedVehicles.isEmpty()) {
                reservation.setVehiculoId(selectedVehicles.get(0).getId());
            }
            reservation.setFechaInicio(startDatePicker.getValue());
            reservation.setFechaFin(endDatePicker.getValue());
            reservation.setNumeroPersonas(Integer.parseInt(peopleCountField.getText()));
            reservation.setPrecioTotal(BigDecimal.valueOf(Double.parseDouble(totalPriceField.getText())));
            reservation.setEstado(statusComboBox.getValue());
            reservation.setNotas(notesArea.getText());

            saveReservation();
        } catch (NumberFormatException e) {
            showAlert("Error", "Datos numéricos inválidos");
        }
    }



    private boolean validateForm() {
        if (userComboBox.getValue() == null) {
            showAlert("Error", "El usuario es requerido");
            return false;
        }

        if (startDatePicker.getValue() == null) {
            showAlert("Error", "La fecha de inicio es requerida");
            return false;
        }

        if (endDatePicker.getValue() == null) {
            showAlert("Error", "La fecha de fin es requerida");
            return false;
        }

        if (startDatePicker.getValue().isAfter(endDatePicker.getValue())) {
            showAlert("Error", "La fecha de inicio debe ser anterior a la fecha de fin");
            return false;
        }

        if (peopleCountField.getText().isEmpty()) {
            showAlert("Error", "El número de personas es requerido");
            return false;
        }

        if (totalPriceField.getText().isEmpty()) {
            showAlert("Error", "El precio total es requerido");
            return false;
        }

        if (statusComboBox.getValue() == null) {
            showAlert("Error", "El estado es requerido");
            return false;
        }

        return true;
    }

    private void saveReservation() {
        new Thread(() -> {
            try {
                var apiClient = SessionManager.getInstance().getApiClient();
                var response = isEditMode ?
                    apiClient.put("/reservations/" + reservation.getId(), reservation, Reservation.class) :
                    apiClient.post("/reservations", reservation, Reservation.class);

                javafx.application.Platform.runLater(() -> {
                    if (response.isSuccess()) {
                        if (onSaveCallback != null) {
                            onSaveCallback.run();
                        }
                    } else {
                        showAlert("Error", "Error al guardar reservación: " + response.getErrorMessage());
                    }
                });
            } catch (IOException e) {
                logger.error("Error saving reservation", e);
                javafx.application.Platform.runLater(() -> {
                    showAlert("Error", "Error de conexión: " + e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    public void handleAddService() {
        String selectedType = serviceTypeComboBox.getValue();
        if (selectedType == null) {
            showAlert("Error", "Selecciona un tipo de servicio");
            return;
        }

        HBox serviceRow = new HBox(10);
        serviceRow.setPadding(new Insets(5));

        Label typeLabel = new Label(selectedType + ":");
        final ComboBox[] serviceComboBox = new ComboBox[1];

        switch (selectedType) {
            case "Paquete":
                serviceComboBox[0] = new ComboBox<>();
                serviceComboBox[0].setItems(FXCollections.observableArrayList(packages));
                serviceComboBox[0].setConverter(new javafx.util.StringConverter<Package>() {
                    @Override
                    public String toString(Package pkg) {
                        return pkg != null ? pkg.getNombre() : "";
                    }
                    @Override
                    public Package fromString(String string) {
                        return null;
                    }
                });
                serviceComboBox[0].setOnAction(e -> {
                    Package selected = (Package) serviceComboBox[0].getValue();
                    if (selected != null) {
                        selectedPackages.add(selected);
                        calculateTotalPrice();
                    }
                });
                break;
            case "Vuelo":
                serviceComboBox[0] = new ComboBox<>();
                serviceComboBox[0].setItems(FXCollections.observableArrayList(flights));
                serviceComboBox[0].setConverter(new javafx.util.StringConverter<Flight>() {
                    @Override
                    public String toString(Flight flight) {
                        return flight != null ? flight.getOrigen() + " - " + flight.getDestino() : "";
                    }
                    @Override
                    public Flight fromString(String string) {
                        return null;
                    }
                });
                serviceComboBox[0].setOnAction(e -> {
                    Flight selected = (Flight) serviceComboBox[0].getValue();
                    if (selected != null) {
                        selectedFlights.add(selected);
                        calculateTotalPrice();
                    }
                });
                break;
            case "Hotel":
                serviceComboBox[0] = new ComboBox<>();
                serviceComboBox[0].setItems(FXCollections.observableArrayList(hotels));
                serviceComboBox[0].setConverter(new javafx.util.StringConverter<Hotel>() {
                    @Override
                    public String toString(Hotel hotel) {
                        return hotel != null ? hotel.getNombre() : "";
                    }
                    @Override
                    public Hotel fromString(String string) {
                        return null;
                    }
                });
                serviceComboBox[0].setOnAction(e -> {
                    Hotel selected = (Hotel) serviceComboBox[0].getValue();
                    if (selected != null) {
                        selectedHotels.add(selected);
                        calculateTotalPrice();
                    }
                });
                break;
            case "Vehículo":
                serviceComboBox[0] = new ComboBox<>();
                serviceComboBox[0].setItems(FXCollections.observableArrayList(vehicles));
                serviceComboBox[0].setConverter(new javafx.util.StringConverter<Vehicle>() {
                    @Override
                    public String toString(Vehicle vehicle) {
                        return vehicle != null ? vehicle.getModelo() : "";
                    }
                    @Override
                    public Vehicle fromString(String string) {
                        return null;
                    }
                });
                serviceComboBox[0].setOnAction(e -> {
                    Vehicle selected = (Vehicle) serviceComboBox[0].getValue();
                    if (selected != null) {
                        selectedVehicles.add(selected);
                        calculateTotalPrice();
                    }
                });
                break;
        }

        Button removeButton = new Button("Eliminar");
        removeButton.setOnAction(e -> {
            servicesContainer.getChildren().remove(serviceRow);
            // Remove from selected lists
            if (serviceComboBox[0].getValue() instanceof Package) {
                selectedPackages.remove((Package) serviceComboBox[0].getValue());
            } else if (serviceComboBox[0].getValue() instanceof Flight) {
                selectedFlights.remove((Flight) serviceComboBox[0].getValue());
            } else if (serviceComboBox[0].getValue() instanceof Hotel) {
                selectedHotels.remove((Hotel) serviceComboBox[0].getValue());
            } else if (serviceComboBox[0].getValue() instanceof Vehicle) {
                selectedVehicles.remove((Vehicle) serviceComboBox[0].getValue());
            }
            calculateTotalPrice();
        });

        serviceRow.getChildren().addAll(typeLabel, serviceComboBox[0], removeButton);
        servicesContainer.getChildren().add(serviceRow);
    }

    private void calculateTotalPrice() {
        BigDecimal total = BigDecimal.ZERO;

        for (Package pkg : selectedPackages) {
            total = total.add(pkg.getPrecio());
        }
        for (Flight flight : selectedFlights) {
            total = total.add(flight.getPrecio());
        }
        for (Hotel hotel : selectedHotels) {
            total = total.add(hotel.getPrecioNoche());
        }
        for (Vehicle vehicle : selectedVehicles) {
            total = total.add(vehicle.getPrecioDia());
        }

        totalPriceField.setText(total.toString());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
