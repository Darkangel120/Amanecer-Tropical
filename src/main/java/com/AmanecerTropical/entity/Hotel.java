package com.AmanecerTropical.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Entity
@Table(name = "hoteles")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nombre;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @NotBlank
    private String ubicacion;

    @NotNull
    @Positive
    @Column(name = "precio_por_noche")
    private BigDecimal precioPorNoche;

    @NotBlank
    @Column(name = "url_imagen")
    private String urlImagen;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String comodidades;

    @NotNull
    private Integer estrellas;

    @NotNull
    @Column(name = "habitaciones_disponibles")
    private Integer habitacionesDisponibles;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "agencia_hotel_id")
    private HotelAgency agenciaHotel;

    @Column(columnDefinition = "SMALLINT DEFAULT 1")
    private boolean activo = true;

    public Hotel() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public BigDecimal getPrecioPorNoche() { return precioPorNoche; }
    public void setPrecioPorNoche(BigDecimal precioPorNoche) { this.precioPorNoche = precioPorNoche; }

    public String getUrlImagen() { return urlImagen; }
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }

    public String getComodidades() { return comodidades; }
    public void setComodidades(String comodidades) { this.comodidades = comodidades; }

    public Integer getEstrellas() { return estrellas; }
    public void setEstrellas(Integer estrellas) { this.estrellas = estrellas; }

    public Integer getHabitacionesDisponibles() { return habitacionesDisponibles; }
    public void setHabitacionesDisponibles(Integer habitacionesDisponibles) { this.habitacionesDisponibles = habitacionesDisponibles; }

    public HotelAgency getAgenciaHotel() { return agenciaHotel; }
    public void setAgenciaHotel(HotelAgency agenciaHotel) { this.agenciaHotel = agenciaHotel; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}