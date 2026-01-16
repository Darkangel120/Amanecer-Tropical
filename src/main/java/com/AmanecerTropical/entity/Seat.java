package com.AmanecerTropical.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "asientos")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "vuelo_id", nullable = false)
    private Flight vuelo;

    @NotBlank
    @Column(name = "numero_asiento")
    private String seatNumber;

    @NotBlank
    private String clase = "economica";

    @Column(columnDefinition = "SMALLINT DEFAULT 1")
    private boolean disponible = true;

    @Column(columnDefinition = "SMALLINT DEFAULT 1")
    private boolean activo = true;

    public Seat() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Flight getVuelo() { return vuelo; }
    public void setVuelo(Flight vuelo) { this.vuelo = vuelo; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public String getClase() { return clase; }
    public void setClase(String clase) { this.clase = clase; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
