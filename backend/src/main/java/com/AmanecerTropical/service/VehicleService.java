package com.AmanecerTropical.service;

import com.AmanecerTropical.entity.Vehicle;
import com.AmanecerTropical.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    public List<Vehicle> getAllActiveVehicles() {
        return vehicleRepository.findByActiveTrue();
    }

    public List<Vehicle> searchVehiclesByType(@NonNull String type) {
        return vehicleRepository.findByType(type);
    }

    public Optional<Vehicle> getVehicleById(@NonNull Long id) {
        return vehicleRepository.findById(id);
    }

    public Vehicle saveVehicle(@NonNull Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public void deleteVehicle(@NonNull Long id) {
        vehicleRepository.deleteById(id);
    }
}
