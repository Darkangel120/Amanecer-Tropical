package com.AmanecerTropical.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    @Lazy
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowCredentials(false);
                    config.addAllowedOrigin("http://127.0.0.1:5500");
                    config.addAllowedOrigin("http://localhost:5500");
                    config.addAllowedOrigin("http://127.0.0.1:3000");
                    config.addAllowedOrigin("http://localhost:3000");
                    config.addAllowedOrigin("http://localhost:8080");
                    config.addAllowedOrigin("http://127.0.0.1:8080");
                    config.addAllowedOrigin("file://");
                    config.addAllowedOrigin("null");
                    config.addAllowedMethod("*");
                    config.addAllowedHeader("*");
                    config.addExposedHeader("Authorization");
                    return config;
                }))
                .authorizeHttpRequests(authz -> authz
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/api/users/register", "/api/users/email/*").permitAll()
                                .requestMatchers("/api/destinations/**", "/api/flights/**", "/api/hotels/**", "/api/vehicles/**", "/api/notifications/**").permitAll()
                                .requestMatchers("/uploads/**").permitAll()
                                .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Only add JWT filter for authenticated endpoints
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
