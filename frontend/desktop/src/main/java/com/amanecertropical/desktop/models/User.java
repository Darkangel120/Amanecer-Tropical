package com.amanecertropical.desktop.models;

import java.time.LocalDate;

public class User {
    private Long id;
    private String name;
    private LocalDate birthdate;
    private String gender;
    private String nationality;
    private String address;
    private String city;
    private String state;
    private String phone;
    private String email;
    private String cedula;
    private String passport;
    private LocalDate passportExpiry;
    private String emergencyName;
    private String emergencyPhone;
    private String emergencyRelationship;
    private String travelStyle;
    private String dietaryRestrictions;
    private String specialNeeds;
    private String password;
    private UserRole role;

    public enum UserRole {
        USER, ADMIN
    }

    // Constructors, getters, setters
    public User() {}

    public User(String name, LocalDate birthdate, String gender, String nationality,
                String address, String city, String state, String phone, String email,
                String cedula, String password) {
        this.name = name;
        this.birthdate = birthdate;
        this.gender = gender;
        this.nationality = nationality;
        this.address = address;
        this.city = city;
        this.state = state;
        this.phone = phone;
        this.email = email;
        this.cedula = cedula;
        this.password = password;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getBirthdate() { return birthdate; }
    public void setBirthdate(LocalDate birthdate) { this.birthdate = birthdate; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public String getPassport() { return passport; }
    public void setPassport(String passport) { this.passport = passport; }

    public LocalDate getPassportExpiry() { return passportExpiry; }
    public void setPassportExpiry(LocalDate passportExpiry) { this.passportExpiry = passportExpiry; }

    public String getEmergencyName() { return emergencyName; }
    public void setEmergencyName(String emergencyName) { this.emergencyName = emergencyName; }

    public String getEmergencyPhone() { return emergencyPhone; }
    public void setEmergencyPhone(String emergencyPhone) { this.emergencyPhone = emergencyPhone; }

    public String getEmergencyRelationship() { return emergencyRelationship; }
    public void setEmergencyRelationship(String emergencyRelationship) { this.emergencyRelationship = emergencyRelationship; }

    public String getTravelStyle() { return travelStyle; }
    public void setTravelStyle(String travelStyle) { this.travelStyle = travelStyle; }

    public String getDietaryRestrictions() { return dietaryRestrictions; }
    public void setDietaryRestrictions(String dietaryRestrictions) { this.dietaryRestrictions = dietaryRestrictions; }

    public String getSpecialNeeds() { return specialNeeds; }
    public void setSpecialNeeds(String specialNeeds) { this.specialNeeds = specialNeeds; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
}
