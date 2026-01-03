package com.AmanecerTropical.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Entity
@Table(name = "hotels")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    @Column(length = 1000)
    private String description;

    @NotBlank
    private String location;

    @NotNull
    @Positive
    private BigDecimal pricePerNight;

    @NotBlank
    private String imageUrl;

    @NotBlank
    private String amenities; // JSON string of amenities

    @NotNull
    private Integer stars; // 1-5 stars

    @NotNull
    private Integer availableRooms;

    @Column(columnDefinition = "SMALLINT DEFAULT 1")
    private boolean active = true;

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

    // Getters and Setters
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

}
