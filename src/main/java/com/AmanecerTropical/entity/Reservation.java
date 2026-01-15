package com.AmanecerTropical.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "reservaciones")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El usuario es requerido")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnoreProperties({"contrasena", "password", "hibernateLazyInitializer", "handler"})
    private User usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paquete_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Package paquete;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vuelo_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Flight vuelo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Hotel hotel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehiculo_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Vehicle vehiculo;

    @NotBlank(message = "El tipo de servicio es requerido")
    @Column(name = "tipo_servicio")
    private String tipoServicio;

    @NotNull(message = "La fecha de inicio es requerida")
    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es requerida")
    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @NotNull(message = "El número de personas es requerido")
    @Positive(message = "El número de personas debe ser positivo")
    @Column(name = "numero_personas")
    private Integer numeroPersonas;

    @NotNull(message = "El precio total es requerido")
    @Positive(message = "El precio total debe ser positivo")
    @Column(name = "precio_total")
    private BigDecimal precioTotal;

    @NotBlank(message = "El estado es requerido")
    private String estado = "pendiente";

    @Column(name = "solicitudes_especiales", columnDefinition = "TEXT")
    private String solicitudesEspeciales;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @OneToMany(mappedBy = "reservacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"reservacion", "hibernateLazyInitializer", "handler"})
    private List<Payment> pagos;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        
        if (paquete == null && vuelo == null && hotel == null && vehiculo == null) {
            throw new IllegalStateException("Debe especificar al menos un servicio (paquete, vuelo, hotel o vehículo)");
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    public Reservation() {}

    public Reservation(User usuario, Package paquete, LocalDate fechaInicio,
                      LocalDate fechaFin, Integer numeroPersonas, BigDecimal precioTotal,
                      String estado, String tipoServicio) {
        this.usuario = usuario;
        this.paquete = paquete;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.numeroPersonas = numeroPersonas;
        this.precioTotal = precioTotal;
        this.estado = estado;
        this.tipoServicio = tipoServicio;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUsuario() { return usuario; }
    public void setUsuario(User usuario) { this.usuario = usuario; }

    public Package getPaquete() { return paquete; }
    public void setPaquete(Package paquete) { this.paquete = paquete; }

    public Flight getVuelo() { return vuelo; }
    public void setVuelo(Flight vuelo) { this.vuelo = vuelo; }

    public Hotel getHotel() { return hotel; }
    public void setHotel(Hotel hotel) { this.hotel = hotel; }

    public Vehicle getVehiculo() { return vehiculo; }
    public void setVehiculo(Vehicle vehiculo) { this.vehiculo = vehiculo; }

    public String getTipoServicio() { return tipoServicio; }
    public void setTipoServicio(String tipoServicio) { this.tipoServicio = tipoServicio; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public Integer getNumeroPersonas() { return numeroPersonas; }
    public void setNumeroPersonas(Integer numeroPersonas) { this.numeroPersonas = numeroPersonas; }

    public BigDecimal getPrecioTotal() { return precioTotal; }
    public void setPrecioTotal(BigDecimal precioTotal) { this.precioTotal = precioTotal; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getSolicitudesEspeciales() { return solicitudesEspeciales; }
    public void setSolicitudesEspeciales(String solicitudesEspeciales) { this.solicitudesEspeciales = solicitudesEspeciales; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    public List<Payment> getPagos() { return pagos; }
    public void setPagos(List<Payment> pagos) { this.pagos = pagos; }
}
