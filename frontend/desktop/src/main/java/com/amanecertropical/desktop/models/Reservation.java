package com.amanecertropical.desktop.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Reservation {
    private Long id;
    private User user;
    private Destination destination;
    private Flight flight;
    private Hotel hotel;
    private String serviceType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer numberOfPeople;
    private BigDecimal totalPrice;
    private String status;
    private String specialRequests;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public Reservation() {}

    public Reservation(User user, Destination destination, LocalDate startDate,
                      LocalDate endDate, Integer numberOfPeople, BigDecimal totalPrice,
                      String status, String serviceType) {
        this.user = user;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.numberOfPeople = numberOfPeople;
        this.totalPrice = totalPrice;
        this.status = status;
        this.serviceType = serviceType;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Destination getDestination() { return destination; }
    public void setDestination(Destination destination) { this.destination = destination; }

    public Flight getFlight() { return flight; }
    public void setFlight(Flight flight) { this.flight = flight; }

    public Hotel getHotel() { return hotel; }
    public void setHotel(Hotel hotel) { this.hotel = hotel; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Integer getNumberOfPeople() { return numberOfPeople; }
    public void setNumberOfPeople(Integer numberOfPeople) { this.numberOfPeople = numberOfPeople; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSpecialRequests() { return specialRequests; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
