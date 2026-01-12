package com.AmanecerTropical.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "vehiculos")
public class Vehicle {

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
    private String tipo;

    @NotNull
    @Positive
    @Column(name = "precio_por_dia")
    private BigDecimal precioPorDia;

    @NotBlank
    @Column(name = "url_imagen")
    private String urlImagen;

    @NotNull
    private Integer capacidad;

    @NotBlank
    private String transmision;

    @NotBlank
    @Column(name = "tipo_combustible")
    private String tipoCombustible;

    @NotNull
    @Column(name = "unidades_disponibles")
    private Integer unidadesDisponibles;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "agencia_vehiculo_id")
    private VehicleAgency agenciaVehiculo;

    @Column(columnDefinition = "SMALLINT DEFAULT 1")
    private boolean activo = true;

    public Vehicle() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public BigDecimal getPrecioPorDia() { return precioPorDia; }
    public void setPrecioPorDia(BigDecimal precioPorDia) { this.precioPorDia = precioPorDia; }

    public String getUrlImagen() { return urlImagen; }
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }

    public Integer getCapacidad() { return capacidad; }
    public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }

    public String getTransmision() { return transmision; }
    public void setTransmision(String transmision) { this.transmision = transmision; }

    public String getTipoCombustible() { return tipoCombustible; }
    public void setTipoCombustible(String tipoCombustible) { this.tipoCombustible = tipoCombustible; }

    public Integer getUnidadesDisponibles() { return unidadesDisponibles; }
    public void setUnidadesDisponibles(Integer unidadesDisponibles) { this.unidadesDisponibles = unidadesDisponibles; }

    public VehicleAgency getAgenciaVehiculo() { return agenciaVehiculo; }
    public void setAgenciaVehiculo(VehicleAgency agenciaVehiculo) { this.agenciaVehiculo = agenciaVehiculo; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}