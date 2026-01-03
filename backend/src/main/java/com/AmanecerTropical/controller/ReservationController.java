package com.AmanecerTropical.controller;

import com.AmanecerTropical.entity.Reservation;
import com.AmanecerTropical.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Reservation>> getAllReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication, #userId)")
    public ResponseEntity<List<Reservation>> getReservationsByUserId(@PathVariable Long userId) {
        List<Reservation> reservations = reservationService.getReservationsByUser(userId);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/destination/{destinationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Reservation>> getReservationsByDestinationId(@PathVariable Long destinationId) {
        List<Reservation> reservations = reservationService.getReservationsByDestination(destinationId);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @reservationSecurity.hasReservationAccess(authentication, #id)")
    public ResponseEntity<Reservation> getReservationById(@PathVariable @NonNull Long id) {
        Optional<Reservation> reservation = reservationService.getReservationById(id);
        return reservation.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Reservation> createReservation(@RequestBody @NonNull Reservation reservation) {
        try {
            Reservation createdReservation = reservationService.createReservation(reservation);
            return ResponseEntity.ok(createdReservation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @reservationSecurity.hasReservationAccess(authentication, #id)")
    public ResponseEntity<Reservation> updateReservation(@PathVariable @NonNull Long id, @RequestBody @NonNull Reservation reservation) {
        Optional<Reservation> existingReservation = reservationService.getReservationById(id);
        if (existingReservation.isPresent()) {
            reservation.setId(id);
            try {
                Reservation updatedReservation = reservationService.updateReservation(reservation);
                return ResponseEntity.ok(updatedReservation);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @reservationSecurity.hasReservationAccess(authentication, #id)")
    public ResponseEntity<Void> deleteReservation(@PathVariable @NonNull Long id) {
        Optional<Reservation> reservation = reservationService.getReservationById(id);
        if (reservation.isPresent()) {
            reservationService.deleteReservation(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/check-availability")
    public ResponseEntity<Boolean> checkDestinationAvailability(@RequestParam Long destinationId,
                                                               @RequestParam String checkInDate,
                                                               @RequestParam String checkOutDate) {
        LocalDate checkIn = LocalDate.parse(checkInDate);
        LocalDate checkOut = LocalDate.parse(checkOutDate);
        boolean available = reservationService.isDestinationAvailable(destinationId, checkIn, checkOut);
        return ResponseEntity.ok(available);
    }

    @GetMapping("/overlapping")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Reservation>> findOverlappingReservations(@RequestParam(required = false) Long destinationId,
                                                                        @RequestParam(required = false) Long flightId,
                                                                        @RequestParam(required = false) Long hotelId,
                                                                        @RequestParam(required = false) Long vehicleId,
                                                                        @RequestParam String checkInDate,
                                                                        @RequestParam String checkOutDate) {
        LocalDate checkIn = LocalDate.parse(checkInDate);
        LocalDate checkOut = LocalDate.parse(checkOutDate);
        List<Reservation> overlappingReservations = reservationService.getOverlappingReservations(destinationId, flightId, hotelId, vehicleId, checkIn, checkOut);
        return ResponseEntity.ok(overlappingReservations);
    }
}
