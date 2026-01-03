package com.AmanecerTropical.service;

import com.AmanecerTropical.entity.Flight;
import com.AmanecerTropical.repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FlightService {

    @Autowired
    private FlightRepository flightRepository;

    public List<Flight> getAllActiveFlights() {
        return flightRepository.findByActiveTrue();
    }

    public List<Flight> searchFlights(@NonNull String origin, String destination) {
        return flightRepository.findByOriginAndDestination(origin, destination);
    }

    public Optional<Flight> getFlightById(@NonNull Long id) {
        return flightRepository.findById(id);
    }

    public Flight saveFlight(@NonNull Flight flight) {
        return flightRepository.save(flight);
    }

    public void deleteFlight(@NonNull Long id) {
        flightRepository.deleteById(id);
    }
}
