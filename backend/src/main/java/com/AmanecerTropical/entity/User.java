package com.AmanecerTropical.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "foto_perfil", length = 500)
    private String fotoPerfil;

    @NotBlank
    @Size(max = 100)
    private String nombre;

    @NotNull
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @NotBlank
    @Size(max = 20)
    private String genero;

    @NotBlank
    @Size(max = 50)
    private String nacionalidad;

    @NotBlank
    @Size(max = 255)
    private String direccion;

    @NotBlank
    @Size(max = 100)
    private String ciudad;

    @NotBlank
    @Size(max = 100)
    private String estado;

    @NotBlank
    @Size(max = 20)
    private String telefono;

    @NotBlank
    @Email
    @Column(name = "correo_electronico", unique = true)
    private String correoElectronico;

    @NotBlank
    @Size(max = 20)
    private String cedula;

    @Size(max = 20)
    private String pasaporte;

    @Column(name = "fecha_expiracion_pasaporte")
    private LocalDate fechaExpiracionPasaporte;

    @Column(name = "nombre_emergencia", length = 100)
    private String nombreEmergencia;

    @Column(name = "telefono_emergencia", length = 20)
    private String telefonoEmergencia;

    @Column(name = "relacion_emergencia", length = 50)
    private String relacionEmergencia;

    @Column(name = "estilo_viaje", length = 50)
    private String estiloViaje;

    @Column(name = "restricciones_dieteticas", length = 255)
    private String restriccionesDieteticas;

    @Column(name = "necesidades_especiales", columnDefinition = "TEXT")
    private String necesidadesEspeciales;

    @NotBlank
    @Size(min = 6)
    private String contrasena;

    @NotBlank
    @Size(max = 20)
    private String rol = "USUARIO";

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // Constructors
    public User() {}

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    // Getters and Setters
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
    
    // Métodos auxiliares para compatibilidad con Spring Security
    public String getEmail() {
        return this.correoElectronico;
    }
    
    public void setEmail(String email) {
        this.correoElectronico = email;
    }
    
    public String getPassword() {
        return this.contrasena;
    }
    
    public void setPassword(String password) {
        this.contrasena = password;
    }
    
    // Método para obtener rol en formato compatible con Spring Security
    public String getRole() {
        return this.rol;
    }
}