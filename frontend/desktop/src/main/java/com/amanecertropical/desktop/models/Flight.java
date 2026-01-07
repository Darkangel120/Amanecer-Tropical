package com.amanecertropical.desktop.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Flight {
    private Long id;
    private String flightNumber;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private BigDecimal price;
    private String airline;
    private String aircraftType;
    private Integer availableSeats;
    private String classType;
    private boolean active;

    // Constructors
    public Flight() {}

    public Flight(String flightNumber, String origin, String destination,
                  LocalDateTime departureTime, LocalDateTime arrivalTime,
                  BigDecimal price, String airline, String aircraftType,
                  Integer availableSeats, String classType) {
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
        this.airline = airline;
        this.aircraftType = aircraftType;
        this.availableSeats = availableSeats;
        this.classType = classType;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }

    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getAirline() { return airline; }
    public void setAirline(String airline) { this.airline = airline; }

    public String getAircraftType() { return aircraftType; }
    public void setAircraftType(String aircraftType) { this.aircraftType = aircraftType; }

    public Integer getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(Integer availableSeats) { this.availableSeats = availableSeats; }

    public String getClassType() { return classType; }
    public void setClassType(String classType) { this.classType = classType; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
