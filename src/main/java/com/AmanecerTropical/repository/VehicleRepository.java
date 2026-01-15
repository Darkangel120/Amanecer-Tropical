package com.AmanecerTropical.repository;

import com.AmanecerTropical.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    
    @Query("SELECT v FROM Vehicle v WHERE v.activo = true")
    List<Vehicle> findByActivoTrue();
    
    List<Vehicle> findByTipo(String tipo);
}