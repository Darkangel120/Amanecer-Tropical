package com.amanecertropical.desktop.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class User {

    private Long id;
    private String fotoPerfil;
    private String nombre;
    private LocalDate fechaNacimiento;
    private String genero;
    private String nacionalidad;
    private String direccion;
    private String ciudad;
    private String estado;
    private String telefono;
    private String correoElectronico;
    private String cedula;
    private String pasaporte;
    private LocalDate fechaExpiracionPasaporte;
    private String nombreEmergencia;
    private String telefonoEmergencia;
    private String relacionEmergencia;
    private String estiloViaje;
    private String restriccionesDieteticas;
    private String necesidadesEspeciales;
    private String contrasena;
    private String rol;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public User() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFotoPerfil() { return fotoPerfil; }
    public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getNacionalidad() { return nacionalidad; }
    public void setNacionalidad(String nacionalidad) { this.nacionalidad = nacionalidad; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreoElectronico() { return correoElectronico; }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = correoElectronico; }

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public String getPasaporte() { return pasaporte; }
    public void setPasaporte(String pasaporte) { this.pasaporte = pasaporte; }

    public LocalDate getFechaExpiracionPasaporte() { return fechaExpiracionPasaporte; }
    public void setFechaExpiracionPasaporte(LocalDate fechaExpiracionPasaporte) { this.fechaExpiracionPasaporte = fechaExpiracionPasaporte; }

    public String getNombreEmergencia() { return nombreEmergencia; }
    public void setNombreEmergencia(String nombreEmergencia) { this.nombreEmergencia = nombreEmergencia; }

    public String getTelefonoEmergencia() { return telefonoEmergencia; }
    public void setTelefonoEmergencia(String telefonoEmergencia) { this.telefonoEmergencia = telefonoEmergencia; }

    public String getRelacionEmergencia() { return relacionEmergencia; }
    public void setRelacionEmergencia(String relacionEmergencia) { this.relacionEmergencia = relacionEmergencia; }

    public String getEstiloViaje() { return estiloViaje; }
    public void setEstiloViaje(String estiloViaje) { this.estiloViaje = estiloViaje; }

    public String getRestriccionesDieteticas() { return restriccionesDieteticas; }
    public void setRestriccionesDieteticas(String restriccionesDieteticas) { this.restriccionesDieteticas = restriccionesDieteticas; }

    public String getNecesidadesEspeciales() { return necesidadesEspeciales; }
    public void setNecesidadesEspeciales(String necesidadesEspeciales) { this.necesidadesEspeciales = necesidadesEspeciales; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    public String getEmail() { return this.correoElectronico; }
    public void setEmail(String email) { this.correoElectronico = email; }

    public String getPassword() { return this.contrasena; }
    public void setPassword(String password) { this.contrasena = password; }

    public String getRole() { return this.rol; }

    @Override
    public String toString() {
        return nombre != null ? nombre : "Usuario sin nombre";
    }
}
