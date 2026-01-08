package com.AmanecerTropical.repository;

import com.AmanecerTropical.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    
    @Query("SELECT f FROM Flight f WHERE f.activo = true")
    List<Flight> findByActivoTrue();
    
    List<Flight> findByOrigenAndDestino(String origen, String destino);

    @Query("SELECT f FROM Flight f WHERE f.origen = :origen AND f.destino = :destino AND DATE(f.departureTime) = :fechaSalida")
    List<Flight> findByOrigenAndDestinoAndFechaSalida(@Param("origen") String origen, 
                                                     @Param("destino") String destino, 
                                                     @Param("fechaSalida") LocalDate fechaSalida);
}