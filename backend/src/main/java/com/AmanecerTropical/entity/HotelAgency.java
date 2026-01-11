package com.AmanecerTropical.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "agencias_hoteles")
public class HotelAgency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "nombre_agencia")
    private String nombreAgencia;

    @NotNull
    @Column(name = "fecha_contrato_inicio")
    private LocalDate fechaContratoInicio;

    @Column(name = "fecha_contrato_fin")
    private LocalDate fechaContratoFin;

    @Column(name = "detalles_contrato", columnDefinition = "TEXT")
    private String detallesContrato;

    private String contacto;

    @Column(columnDefinition = "SMALLINT DEFAULT 1")
    private boolean activo = true;

    @OneToMany(mappedBy = "agenciaHotel")
    @JsonIgnore
    private List<Hotel> hoteles;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreAgencia() { return nombreAgencia; }
    public void setNombreAgencia(String nombreAgencia) { this.nombreAgencia = nombreAgencia; }

    public LocalDate getFechaContratoInicio() { return fechaContratoInicio; }
    public void setFechaContratoInicio(LocalDate fechaContratoInicio) { this.fechaContratoInicio = fechaContratoInicio; }

    public LocalDate getFechaContratoFin() { return fechaContratoFin; }
    public void setFechaContratoFin(LocalDate fechaContratoFin) { this.fechaContratoFin = fechaContratoFin; }

    public String getDetallesContrato() { return detallesContrato; }
    public void setDetallesContrato(String detallesContrato) { this.detallesContrato = detallesContrato; }

    public String getContacto() { return contacto; }
    public void setContacto(String contacto) { this.contacto = contacto; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public List<Hotel> getHoteles() { return hoteles; }
    public void setHoteles(List<Hotel> hoteles) { this.hoteles = hoteles; }
}