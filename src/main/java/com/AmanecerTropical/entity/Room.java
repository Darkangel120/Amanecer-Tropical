package com.AmanecerTropical.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Entity
@Table(name = "habitaciones")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @NotBlank
    @Column(name = "numero_habitacion")
    private String numeroHabitacion;

    @NotBlank
    @Column(name = "tipo_habitacion")
    private String tipoHabitacion;

    @NotNull
    @Positive
    private Integer capacidad;

    @NotNull
    @Positive
    @Column(name = "precio_por_noche")
    private BigDecimal precioPorNoche;

    @Column(columnDefinition = "TEXT")
    private String comodidades;

    @Column(name = "disponible")
    private Integer disponible = 1;

    @Column(name = "activo")
    private Integer activo = 1;

    public Room() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Hotel getHotel() { return hotel; }
    public void setHotel(Hotel hotel) { this.hotel = hotel; }

    public String getNumeroHabitacion() { return numeroHabitacion; }
    public void setNumeroHabitacion(String numeroHabitacion) { this.numeroHabitacion = numeroHabitacion; }

    public String getTipoHabitacion() { return tipoHabitacion; }
    public void setTipoHabitacion(String tipoHabitacion) { this.tipoHabitacion = tipoHabitacion; }

    public Integer getCapacidad() { return capacidad; }
    public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }

    public BigDecimal getPrecioPorNoche() { return precioPorNoche; }
    public void setPrecioPorNoche(BigDecimal precioPorNoche) { this.precioPorNoche = precioPorNoche; }

    public String getComodidades() { return comodidades; }
    public void setComodidades(String comodidades) { this.comodidades = comodidades; }

    public Integer getDisponible() { return disponible; }
    public void setDisponible(Integer disponible) { this.disponible = disponible; }
    
    public boolean isDisponible() { 
        return disponible != null && disponible == 1; 
    }
    
    public void setDisponible(boolean disponible) {
        this.disponible = disponible ? 1 : 0;
    }

    public Integer getActivo() { return activo; }
    public void setActivo(Integer activo) { this.activo = activo; }
    
    public boolean isActivo() { 
        return activo != null && activo == 1; 
    }
    
    public void setActivo(boolean activo) {
        this.activo = activo ? 1 : 0;
    }
}