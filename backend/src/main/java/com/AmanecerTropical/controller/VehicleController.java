package com.AmanecerTropical.controller;

import com.AmanecerTropical.entity.Vehicle;
import com.AmanecerTropical.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.lang.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllActiveVehicles() {
        List<Vehicle> vehicles = vehicleService.getAllActiveVehicles();
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Vehicle>> searchVehicles(@RequestParam @NonNull String tipo) {
        List<Vehicle> vehicles = vehicleService.searchVehiclesByType(tipo);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable @NonNull Long id) {
        Optional<Vehicle> vehicle = vehicleService.getVehicleById(id);
        return vehicle.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Vehicle> createVehicle(@Valid @RequestBody @NonNull Vehicle vehicle) {
        Vehicle createdVehicle = vehicleService.saveVehicle(vehicle);
        return ResponseEntity.ok(createdVehicle);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable @NonNull Long id, @Valid @RequestBody @NonNull Vehicle vehicle) {
        vehicle.setId(id);
        Vehicle updatedVehicle = vehicleService.saveVehicle(vehicle);
        return ResponseEntity.ok(updatedVehicle);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteVehicle(@PathVariable @NonNull Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }
}