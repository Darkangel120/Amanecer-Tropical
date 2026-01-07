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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Optional<User> user = userService.getUserByEmail(userDetails.getUsername());

            if (user.isPresent()) {
                String token = jwtUtil.generateToken(user.get().getEmail(), user.get().getRole().name());

                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("user", user.get());

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body("User not found");
            }
        } catch (org.springframework.security.core.AuthenticationException e) {
            // Log the specific authentication exception
            System.err.println("Authentication failed for email: " + loginRequest.getEmail() + ", error: " + e.getMessage());
            return ResponseEntity.badRequest().body("Credenciales inválidas");
        } catch (Exception e) {
            // Log other exceptions
            System.err.println("Unexpected error during login for email: " + loginRequest.getEmail() + ", error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error interno del servidor");
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
}
