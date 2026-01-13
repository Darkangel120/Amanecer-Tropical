package com.amanecertropical.desktop.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {

    private Long id;
    private BigDecimal monto;
    private String metodoPago;
    private String referenciaPago;
    private String estadoPago;
    private LocalDateTime fechaPago;
    private Long reservacionId;
    private Long usuarioId;

    private Reservation reservacion;
    private User usuario;

    public Payment() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public String getReferenciaPago() { return referenciaPago; }
    public void setReferenciaPago(String referenciaPago) { this.referenciaPago = referenciaPago; }

    public String getEstadoPago() { return estadoPago; }
    public void setEstadoPago(String estadoPago) { this.estadoPago = estadoPago; }

    public LocalDateTime getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }

    public Long getReservacionId() { return reservacionId; }
    public void setReservacionId(Long reservacionId) { this.reservacionId = reservacionId; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Reservation getReservacion() { return reservacion; }
    public void setReservacion(Reservation reservacion) { this.reservacion = reservacion; }

    public User getUsuario() { return usuario; }
    public void setUsuario(User usuario) { this.usuario = usuario; }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", monto=" + monto +
                ", metodoPago='" + metodoPago + '\'' +
                ", estadoPago='" + estadoPago + '\'' +
                '}';
    }
}
