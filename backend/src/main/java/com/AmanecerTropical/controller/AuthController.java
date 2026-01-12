package com.AmanecerTropical.controller;

import com.AmanecerTropical.config.JwtUtil;
import com.AmanecerTropical.entity.User;
import com.AmanecerTropical.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @SuppressWarnings("null")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Optional<User> user = userService.getUserByEmail(userDetails.getUsername());

            if (user.isPresent()) {
                String token = jwtUtil.generateToken(user.get().getCorreoElectronico(), user.get().getRol());

                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("user", user.get());

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body("Usuario no encontrado");
            }
        } catch (org.springframework.security.core.AuthenticationException e) {
            System.err.println("Authentication failed for email: " + loginRequest.getEmail() + ", error: " + e.getMessage());
            return ResponseEntity.badRequest().body("Credenciales inválidas");
        } catch (Exception e) {
            System.err.println("Unexpected error during login for email: " + loginRequest.getEmail() + ", error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error interno del servidor");
        }
    }

    @SuppressWarnings("null")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            if (userService.existsByEmail(registerRequest.getEmail())) {
                return ResponseEntity.badRequest().body("El correo electrónico ya está registrado");
            }

            if (userService.existsByCedula(registerRequest.getCedula())) {
                return ResponseEntity.badRequest().body("La cédula ya está registrada");
            }

            User newUser = new User();
            newUser.setNombre(registerRequest.getNombre());
            newUser.setCorreoElectronico(registerRequest.getEmail());
            newUser.setContrasena(registerRequest.getPassword());
            newUser.setCedula(registerRequest.getCedula());
            newUser.setFechaNacimiento(registerRequest.getFechaNacimiento());
            newUser.setGenero(registerRequest.getGenero());
            newUser.setNacionalidad(registerRequest.getNacionalidad());
            newUser.setDireccion(registerRequest.getDireccion());
            newUser.setCiudad(registerRequest.getCiudad());
            newUser.setEstado(registerRequest.getEstado());
            newUser.setTelefono(registerRequest.getTelefono());
            
            newUser.setRol("USUARIO");

            User createdUser = userService.createUser(newUser);

            String token = jwtUtil.generateToken(createdUser.getCorreoElectronico(), createdUser.getRol());

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", createdUser);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error during registration: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error al registrar usuario");
        }
    }

    @SuppressWarnings("null")
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Token no proporcionado");
            }

            String token = authHeader.substring(7);

            String username = jwtUtil.extractUsername(token);

            if (jwtUtil.validateToken(token, username)) {
                Optional<User> user = userService.getUserByEmail(username);
                if (user.isPresent()) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("valid", true);
                    response.put("user", user.get());
                    return ResponseEntity.ok(response);
                } else {
                    return ResponseEntity.status(401).body("Usuario no encontrado");
                }
            } else {
                return ResponseEntity.status(401).body("Token inválido o expirado");
            }
        } catch (Exception e) {
            System.err.println("Error during token validation: " + e.getMessage());
            return ResponseEntity.status(401).body("Error al validar token");
        }
    }

    public static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class RegisterRequest {
        private String nombre;
        private String email;
        private String password;
        private String cedula;
        private java.time.LocalDate fechaNacimiento;
        private String genero;
        private String nacionalidad;
        private String direccion;
        private String ciudad;
        private String estado;
        private String telefono;

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getCedula() { return cedula; }
        public void setCedula(String cedula) { this.cedula = cedula; }

        public java.time.LocalDate getFechaNacimiento() { return fechaNacimiento; }
        public void setFechaNacimiento(java.time.LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

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
    }
}