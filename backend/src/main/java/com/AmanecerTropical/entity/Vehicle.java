package com.AmanecerTropical.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    @Column(length = 1000)
    private String description;

    @NotBlank
    private String type; // car, van, motorcycle, etc.

    @NotNull
    @Positive
    private BigDecimal pricePerDay;

    @NotBlank
    private String imageUrl;

    @NotNull
    private Integer capacity; // number of passengers

    @NotBlank
    private String transmission; // manual, automatic

    @NotBlank
    private String fuelType; // gasoline, diesel, electric

    @NotNull
    private Integer availableUnits;

    @Column(columnDefinition = "SMALLINT DEFAULT 1")
    private boolean active = true;

    // Constructors
    public Vehicle() {}

    public Vehicle(String name, String description, String type,
                   BigDecimal pricePerDay, String imageUrl, Integer capacity,
                   String transmission, String fuelType, Integer availableUnits) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.pricePerDay = pricePerDay;
        this.imageUrl = imageUrl;
        this.capacity = capacity;
        this.transmission = transmission;
        this.fuelType = fuelType;
        this.availableUnits = availableUnits;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public BigDecimal getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(BigDecimal pricePerDay) { this.pricePerDay = pricePerDay; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public String getTransmission() { return transmission; }
    public void setTransmission(String transmission) { this.transmission = transmission; }

    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }

    public Integer getAvailableUnits() { return availableUnits; }
    public void setAvailableUnits(Integer availableUnits) { this.availableUnits = availableUnits; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
