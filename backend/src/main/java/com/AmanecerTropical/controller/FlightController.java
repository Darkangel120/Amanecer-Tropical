package com.AmanecerTropical.controller;

import com.AmanecerTropical.entity.Flight;
import com.AmanecerTropical.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    @Autowired
    private FlightService flightService;

    @GetMapping
    public ResponseEntity<List<Flight>> getAllActiveFlights() {
        List<Flight> flights = flightService.getAllActiveFlights();
        return ResponseEntity.ok(flights);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Flight> getFlightById(@PathVariable @NonNull Long id) {
        Optional<Flight> flight = flightService.getFlightById(id);
        return flight.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Flight> createFlight(@RequestBody @NonNull Flight flight) {
        Flight createdFlight = flightService.saveFlight(flight);
        return ResponseEntity.ok(createdFlight);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Flight> updateFlight(@PathVariable @NonNull Long id, @RequestBody @NonNull Flight flight) {
        flight.setId(id);
        Flight updatedFlight = flightService.saveFlight(flight);
        return ResponseEntity.ok(updatedFlight);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFlight(@PathVariable @NonNull Long id) {
        flightService.deleteFlight(id);
        return ResponseEntity.noContent().build();
    }
}
