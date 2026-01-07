package com.amanecertropical.desktop.models;

import java.math.BigDecimal;

public class Hotel {
    private Long id;
    private String name;
    private String description;
    private String location;
    private BigDecimal pricePerNight;
    private String imageUrl;
    private String amenities;
    private Integer stars;
    private Integer availableRooms;
    private boolean active;

    // Constructors
    public Hotel() {}

    public Hotel(String name, String description, String location,
                 BigDecimal pricePerNight, String imageUrl, String amenities,
                 Integer stars, Integer availableRooms) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.pricePerNight = pricePerNight;
        this.imageUrl = imageUrl;
        this.amenities = amenities;
        this.stars = stars;
        this.availableRooms = availableRooms;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public BigDecimal getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(BigDecimal pricePerNight) { this.pricePerNight = pricePerNight; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getAmenities() { return amenities; }
    public void setAmenities(String amenities) { this.amenities = amenities; }

    public Integer getStars() { return stars; }
    public void setStars(Integer stars) { this.stars = stars; }

    public Integer getAvailableRooms() { return availableRooms; }
    public void setAvailableRooms(Integer availableRooms) { this.availableRooms = availableRooms; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
