package com.amanecertropical.desktop.controller;

import java.math.BigDecimal;

import com.amanecertropical.desktop.models.Hotel;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class HotelDialogController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField locationField;

    @FXML
    private TextField pricePerNightField;

    @FXML
    private TextField starsField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private TextArea amenitiesField;

    @FXML
    private TextField imageUrlField;

    private Hotel hotel;

    @FXML
    public void initialize() {
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
        if (hotel != null) {
            nameField.setText(hotel.getName());
            locationField.setText(hotel.getLocation());
            pricePerNightField.setText(hotel.getPricePerNight().toString());
            starsField.setText(String.valueOf(hotel.getStars()));
            descriptionField.setText(hotel.getDescription());
            amenitiesField.setText(hotel.getAmenities());
            imageUrlField.setText(hotel.getImageUrl());
        }
    }

    public Hotel getHotel() {
        if (hotel == null) {
            hotel = new Hotel();
        }
        hotel.setName(nameField.getText());
        hotel.setLocation(locationField.getText());
        hotel.setPricePerNight(BigDecimal.valueOf(Double.parseDouble(pricePerNightField.getText())));
        hotel.setStars(Integer.parseInt(starsField.getText()));
        hotel.setDescription(descriptionField.getText());
        hotel.setAmenities(amenitiesField.getText());
        hotel.setImageUrl(imageUrlField.getText());
        return hotel;
    }
}
