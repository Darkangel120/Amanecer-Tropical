package com.AmanecerTropical.dto;

import java.math.BigDecimal;

public class RoomDTO {
    private Long id;
    private Long hotelId;
    private String numeroHabitacion;
    private String tipoHabitacion;
    private Integer capacidad;
    private BigDecimal precioPorNoche;
    private String comodidades;
    private Integer disponible;
    private Integer activo;

    public RoomDTO(Long id, Long hotelId, String numeroHabitacion, String tipoHabitacion, 
                   Integer capacidad, BigDecimal precioPorNoche, String comodidades, 
                   Integer disponible, Integer activo) {
        this.id = id;
        this.hotelId = hotelId;
        this.numeroHabitacion = numeroHabitacion;
        this.tipoHabitacion = tipoHabitacion;
        this.capacidad = capacidad;
        this.precioPorNoche = precioPorNoche;
        this.comodidades = comodidades;
        this.disponible = disponible;
        this.activo = activo;
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getHotelId() { return hotelId; }
    public void setHotelId(Long hotelId) { this.hotelId = hotelId; }
    
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
    
    public boolean isDisponible() { return disponible != null && disponible == 1; }
    
    public Integer getActivo() { return activo; }
    public void setActivo(Integer activo) { this.activo = activo; }
    
    public boolean isActivo() { return activo != null && activo == 1; }
}