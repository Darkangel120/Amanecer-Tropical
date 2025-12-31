package com.AmanecerTropical.service;

import com.AmanecerTropical.entity.Reservation;
import com.AmanecerTropical.repository.ReservationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    public List<Reservation> getReservationsByUser(Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    public List<Reservation> getReservationsByDestination(Long destinationId) {
        return reservationRepository.findByDestinationId(destinationId);
    }

    public List<Reservation> getReservationsByStatus(String status) {
        return reservationRepository.findByStatus(status);
    }

    public List<Reservation> getReservationsByUserAndStatus(Long userId, String status) {
        return reservationRepository.findByUserIdAndStatus(userId, status);
    }

    public Reservation createReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public Reservation updateReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }

    public boolean isDestinationAvailable(Long destinationId, LocalDate startDate, LocalDate endDate) {
        long overlappingCount = reservationRepository.countOverlappingReservations(destinationId, startDate, endDate);
        return overlappingCount == 0;
    }

    public List<Reservation> getOverlappingReservations(Long destinationId, LocalDate startDate, LocalDate endDate) {
        return reservationRepository.findOverlappingReservations(destinationId, startDate, endDate);
    }

    public List<Reservation> getRecentReservations(LocalDate startDate) {
        return reservationRepository.findRecentReservations(startDate);
    }
}
