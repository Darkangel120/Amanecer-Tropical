package com.AmanecerTropical.service;

import com.AmanecerTropical.entity.Notification;
import com.AmanecerTropical.entity.Reservation;
import com.AmanecerTropical.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
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

    @Autowired
    private NotificationService notificationService;

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> getReservationById(@NonNull Long id) {
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

    public Reservation createReservation(@NonNull Reservation reservation) {
        Reservation createdReservation = reservationRepository.save(reservation);

        // Create notification for new reservation
        Notification notification = new Notification(
            createdReservation.getUser(),
            "Nueva Reserva",
            "Tu reserva para " + createdReservation.getDestination().getName() + " ha sido creada y está pendiente de confirmación.",
            "reservation"
        );
        notificationService.saveNotification(notification);

        return createdReservation;
    }

    public Reservation updateReservation(@NonNull Reservation reservation) {
        Reservation updatedReservation = reservationRepository.save(reservation);

        // Create notification for status changes
        if (reservation.getStatus().equals("confirmed")) {
            Notification notification = new Notification(
                updatedReservation.getUser(),
                "Reserva Confirmada",
                "Tu reserva para " + updatedReservation.getDestination().getName() + " ha sido confirmada.",
                "reservation"
            );
            notificationService.saveNotification(notification);
        } else if (reservation.getStatus().equals("cancelled")) {
            Notification notification = new Notification(
                updatedReservation.getUser(),
                "Reserva Cancelada",
                "Tu reserva para " + updatedReservation.getDestination().getName() + " ha sido cancelada.",
                "reservation"
            );
            notificationService.saveNotification(notification);
        }

        return updatedReservation;
    }

    public void deleteReservation(@NonNull Long id) {
        reservationRepository.deleteById(id);
    }

    public boolean isDestinationAvailable(Long destinationId, LocalDate startDate, LocalDate endDate) {
        long overlappingCount = reservationRepository.countOverlappingReservations(destinationId, null, null, null, startDate, endDate);
        return overlappingCount == 0;
    }

    public boolean isFlightAvailable(Long flightId, LocalDate startDate, LocalDate endDate) {
        long overlappingCount = reservationRepository.countOverlappingReservations(null, flightId, null, null, startDate, endDate);
        return overlappingCount == 0;
    }

    public boolean isHotelAvailable(Long hotelId, LocalDate startDate, LocalDate endDate) {
        long overlappingCount = reservationRepository.countOverlappingReservations(null, null, hotelId, null, startDate, endDate);
        return overlappingCount == 0;
    }

    public boolean isVehicleAvailable(Long vehicleId, LocalDate startDate, LocalDate endDate) {
        long overlappingCount = reservationRepository.countOverlappingReservations(null, null, null, vehicleId, startDate, endDate);
        return overlappingCount == 0;
    }

    public List<Reservation> getOverlappingReservations(Long destinationId, Long flightId, Long hotelId, Long vehicleId, LocalDate startDate, LocalDate endDate) {
        return reservationRepository.findOverlappingReservations(destinationId, flightId, hotelId, vehicleId, startDate, endDate);
    }

    public List<Reservation> getRecentReservations(LocalDate startDate) {
        return reservationRepository.findRecentReservations(startDate);
    }
}
