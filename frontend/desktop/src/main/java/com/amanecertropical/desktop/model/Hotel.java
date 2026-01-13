package com.amanecertropical.desktop.model;

import java.math.BigDecimal;

public class Hotel {

    private Long id;
    private String nombre;
    private String ubicacion;
    private String descripcion;
    private Integer estrellas;
    private BigDecimal precioNoche;
    private Integer habitacionesDisponibles;
    private Boolean disponible;
    private String servicios;
    private String imagenUrl;

    public Hotel() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Integer getEstrellas() { return estrellas; }
    public void setEstrellas(Integer estrellas) { this.estrellas = estrellas; }

    public BigDecimal getPrecioNoche() { return precioNoche; }
    public void setPrecioNoche(BigDecimal precioNoche) { this.precioNoche = precioNoche; }

    public Integer getHabitacionesDisponibles() { return habitacionesDisponibles; }
    public void setHabitacionesDisponibles(Integer habitacionesDisponibles) { this.habitacionesDisponibles = habitacionesDisponibles; }

    public Boolean getDisponible() { return disponible; }
    public void setDisponible(Boolean disponible) { this.disponible = disponible; }

    public String getServicios() { return servicios; }
    public void setServicios(String servicios) { this.servicios = servicios; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    @Override
    public String toString() {
        return nombre != null ? nombre + " (" + estrellas + "★)" : "Hotel";
    }
}
