package com.AmanecerTropical.service;

import com.AmanecerTropical.entity.Flight;
import com.AmanecerTropical.repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FlightService {

    @Autowired
    private FlightRepository flightRepository;

    public List<Flight> getAllActiveFlights() {
        return flightRepository.findByActivoTrue();
    }

    public List<Flight> searchFlights(@NonNull String origen, String destino) {
        return flightRepository.findByOrigenAndDestino(origen, destino);
    }

    public List<Flight> searchFlightsByDate(@NonNull String origen, String destino, LocalDate fechaSalida) {
        return flightRepository.findByOrigenAndDestinoAndFechaSalida(origen, destino, fechaSalida);
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