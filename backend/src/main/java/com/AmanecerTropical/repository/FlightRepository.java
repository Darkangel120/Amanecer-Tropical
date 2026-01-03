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
    @Query("SELECT f FROM Flight f WHERE f.active = true")
    List<Flight> findByActiveTrue();
    List<Flight> findByOriginAndDestination(String origin, String destination);

    @Query("SELECT f FROM Flight f WHERE f.origin = :origin AND f.destination = :destination AND DATE(f.departureTime) = :departureDate")
    List<Flight> findByOriginDestinationAndDepartureDate(@Param("origin") String origin, @Param("destination") String destination, @Param("departureDate") LocalDate departureDate);
}
