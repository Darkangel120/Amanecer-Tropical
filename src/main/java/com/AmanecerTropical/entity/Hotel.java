package com.AmanecerTropical.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

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

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Room> habitaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "agencia_hotel_id")
    private HotelAgency agenciaHotel;

    @Column(columnDefinition = "SMALLINT DEFAULT 1")
    private Integer activo = 1;

    public Hotel() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public String getUrlImagen() { return urlImagen; }
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }

    public String getComodidades() { return comodidades; }
    public void setComodidades(String comodidades) { this.comodidades = comodidades; }

    public Integer getEstrellas() { return estrellas; }
    public void setEstrellas(Integer estrellas) { this.estrellas = estrellas; }

    public Integer getHabitacionesDisponibles() { return habitacionesDisponibles; }
    public void setHabitacionesDisponibles(Integer habitacionesDisponibles) { this.habitacionesDisponibles = habitacionesDisponibles; }

    public List<Room> getHabitaciones() { return habitaciones; }
    public void setHabitaciones(List<Room> habitaciones) { this.habitaciones = habitaciones; }

    public HotelAgency getAgenciaHotel() { return agenciaHotel; }
    public void setAgenciaHotel(HotelAgency agenciaHotel) { this.agenciaHotel = agenciaHotel; }

    public boolean isActivo() {
        return activo != null && activo == 1;
    }
    public void setActivo(boolean activo) {
        this.activo = activo ? 1 : 0;
    }

    // Método para obtener el precio mínimo de las habitaciones disponibles
    @JsonProperty("precioMinimo")
    public BigDecimal getPrecioMinimo() {
        if (habitaciones == null || habitaciones.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return habitaciones.stream()
                .filter(Room::isDisponible)
                .map(Room::getPrecioPorNoche)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }
}
