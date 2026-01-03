package com.AmanecerTropical.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Entity
@Table(name = "destinations")
public class Destination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String name;

    @NotBlank
    @Column(length = 1000)
    private String description;

    @NotBlank
    private String location;

    @NotNull
    @Positive
    private BigDecimal price;

    @NotBlank
    private String imageUrl;

    @NotBlank
    private String category; // aventura, relax, cultural, etc.

    @NotNull
    private Integer durationDays;

    @NotBlank
    private String includes; // JSON string of included services

    @NotBlank
    private String itinerary; // JSON string of itinerary

    @Column(columnDefinition = "SMALLINT DEFAULT 1")
    private boolean active = true;

    // Constructors
    public Destination() {}

    public Destination(String name, String description, String location, BigDecimal price,
                      String imageUrl, String category, Integer durationDays,
                      String includes, String itinerary) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
        this.durationDays = durationDays;
        this.includes = includes;
        this.itinerary = itinerary;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Integer getDurationDays() { return durationDays; }
    public void setDurationDays(Integer durationDays) { this.durationDays = durationDays; }

    public String getIncludes() { return includes; }
    public void setIncludes(String includes) { this.includes = includes; }

    public String getItinerary() { return itinerary; }
    public void setItinerary(String itinerary) { this.itinerary = itinerary; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
