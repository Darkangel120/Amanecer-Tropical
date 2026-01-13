package com.amanecertropical.desktop.controller;

import com.amanecertropical.desktop.model.Reservation;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ReservationDetailsController implements Initializable {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(ReservationDetailsController.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private Label idLabel;
    @FXML private Label clientLabel;
    @FXML private Label destinationLabel;
    @FXML private Label startDateLabel;
    @FXML private Label endDateLabel;
    @FXML private Label peopleCountLabel;
    @FXML private Label totalPriceLabel;
    @FXML private Label statusLabel;
    @FXML private Label packageLabel;
    @FXML private Label flightLabel;
    @FXML private Label hotelLabel;
    @FXML private Label vehicleLabel;
    @FXML private Label notesLabel;
    @FXML private Label creationDateLabel;
    @FXML private Label updateDateLabel;

    private Reservation reservation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
        populateFields();
    }

    private void populateFields() {
        if (reservation != null) {
            idLabel.setText(reservation.getId() != null ? reservation.getId().toString() : "N/A");
            clientLabel.setText(reservation.getUsuario() != null ? reservation.getUsuario().getNombre() : "N/A");

            String destination = "N/A";
            if (reservation.getPaquete() != null && reservation.getPaquete().getUbicacion() != null) {
                destination = reservation.getPaquete().getUbicacion();
            } else if (reservation.getHotel() != null && reservation.getHotel().getUbicacion() != null) {
                destination = reservation.getHotel().getUbicacion();
            }
            destinationLabel.setText(destination);

            startDateLabel.setText(reservation.getFechaInicio() != null ? reservation.getFechaInicio().format(DATE_FORMATTER) : "N/A");
            endDateLabel.setText(reservation.getFechaFin() != null ? reservation.getFechaFin().format(DATE_FORMATTER) : "N/A");
            peopleCountLabel.setText(reservation.getNumeroPersonas() != null ? reservation.getNumeroPersonas().toString() : "N/A");
            totalPriceLabel.setText(reservation.getPrecioTotal() != null ? "$" + reservation.getPrecioTotal().toString() : "N/A");
            statusLabel.setText(reservation.getEstado() != null ? reservation.getEstado() : "N/A");

            packageLabel.setText(reservation.getPaquete() != null ? reservation.getPaquete().getNombre() : "Ninguno");
            flightLabel.setText(reservation.getVuelo() != null ? reservation.getVuelo().getOrigen() + " - " + reservation.getVuelo().getDestino() : "Ninguno");
            hotelLabel.setText(reservation.getHotel() != null ? reservation.getHotel().getNombre() : "Ninguno");
            vehicleLabel.setText(reservation.getVehiculo() != null ? reservation.getVehiculo().getModelo() : "Ninguno");

            notesLabel.setText(reservation.getNotas() != null && !reservation.getNotas().isEmpty() ? reservation.getNotas() : "Sin notas");

            creationDateLabel.setText(reservation.getFechaCreacion() != null ? reservation.getFechaCreacion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A");
            updateDateLabel.setText(reservation.getFechaActualizacion() != null ? reservation.getFechaActualizacion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A");
        }
    }

    @FXML
    private void handleClose() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) idLabel.getScene().getWindow();
        stage.close();
    }
}
