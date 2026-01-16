package com.AmanecerTropical.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Entity
@Table(name = "paquetes")
public class Package {

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
    private BigDecimal precio;

    @NotBlank
    @Column(name = "url_imagen")
    private String urlImagen;

    @NotBlank
    private String categoria;

    @NotNull
    @Column(name = "duracion_dias")
    private Integer duracionDias;

    @Column(name = "fecha_inicio")
    private java.time.LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private java.time.LocalDate fechaFin;

    @NotNull
    @Column(name = "numero_personas")
    private Integer numeroPersonas;

    @NotNull
    @Column(name = "cantidad_disponible", columnDefinition = "INTEGER DEFAULT 0")
    private Integer cantidadDisponible = 0;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String incluye;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String itinerario;

    @NotNull
    private Integer popularidad = 0;

    @Column(columnDefinition = "SMALLINT DEFAULT 1")
    private boolean activo = true;

    public Package() {}

    public Package(String nombre, String descripcion, String ubicacion,
                   BigDecimal precio, String urlImagen, String categoria,
                   Integer duracionDias, String incluye, String itinerario) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.ubicacion = ubicacion;
        this.precio = precio;
        this.urlImagen = urlImagen;
        this.categoria = categoria;
        this.duracionDias = duracionDias;
        this.incluye = incluye;
        this.itinerario = itinerario;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public String getUrlImagen() { return urlImagen; }
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Integer getDuracionDias() { return duracionDias; }
    public void setDuracionDias(Integer duracionDias) { this.duracionDias = duracionDias; }

    public java.time.LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(java.time.LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public java.time.LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(java.time.LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public Integer getNumeroPersonas() { return numeroPersonas; }
    public void setNumeroPersonas(Integer numeroPersonas) { this.numeroPersonas = numeroPersonas; }

    public Integer getCantidadDisponible() { return cantidadDisponible; }
    public void setCantidadDisponible(Integer cantidadDisponible) { this.cantidadDisponible = cantidadDisponible; }

    public String getIncluye() { return incluye; }
    public void setIncluye(String incluye) { this.incluye = incluye; }

    public String getItinerario() { return itinerario; }
    public void setItinerario(String itinerario) { this.itinerario = itinerario; }

    public Integer getPopularidad() { return popularidad; }
    public void setPopularidad(Integer popularidad) { this.popularidad = popularidad; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}