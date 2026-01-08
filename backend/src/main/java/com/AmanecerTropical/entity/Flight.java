package com.AmanecerTropical.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vuelos")
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "numero_vuelo")
    private String flightNumber;

    @NotBlank
    private String origen;

    @NotBlank
    private String destino;

    @NotNull
    @Column(name = "hora_salida")
    private LocalDateTime departureTime;

    @NotNull
    @Column(name = "hora_llegada")
    private LocalDateTime arrivalTime;

    @NotNull
    @Positive
    private BigDecimal precio;

    @NotBlank
    private String aerolinea;

    @NotBlank
    @Column(name = "tipo_avion")
    private String aircraftType;

    @NotNull
    @Column(name = "asientos_disponibles")
    private Integer availableSeats;

    @NotBlank
    @Column(name = "tipo_clase")
    private String classType = "economica";

    @ManyToOne
    @JoinColumn(name = "agencia_vuelo_id")
    private FlightAgency agenciaVuelo;

    @Column(columnDefinition = "SMALLINT DEFAULT 1")
    private boolean activo = true;

    // Constructors
    public Flight() {}

    // Getters and Setters (ajustados para español)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

    public String getOrigen() { return origen; }
    public void setOrigen(String origen) { this.origen = origen; }

    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }

    public LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }

    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public String getAerolinea() { return aerolinea; }
    public void setAerolinea(String aerolinea) { this.aerolinea = aerolinea; }

    public String getAircraftType() { return aircraftType; }
    public void setAircraftType(String aircraftType) { this.aircraftType = aircraftType; }

    public Integer getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(Integer availableSeats) { this.availableSeats = availableSeats; }

    public String getClassType() { return classType; }
    public void setClassType(String classType) { this.classType = classType; }

    public FlightAgency getAgenciaVuelo() { return agenciaVuelo; }
    public void setAgenciaVuelo(FlightAgency agenciaVuelo) { this.agenciaVuelo = agenciaVuelo; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}