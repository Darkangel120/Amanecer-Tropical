package com.AmanecerTropical.service;

import com.AmanecerTropical.entity.Vehicle;
import com.AmanecerTropical.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    public List<Vehicle> getAllActiveVehicles() {
        return vehicleRepository.findByActivoTrue();
    }

    public List<Vehicle> searchVehiclesByType(@NonNull String tipo) {
        return vehicleRepository.findByTipo(tipo);
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