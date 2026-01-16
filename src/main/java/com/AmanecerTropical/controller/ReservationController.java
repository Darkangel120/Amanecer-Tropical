package com.AmanecerTropical.controller;

import com.AmanecerTropical.entity.Reservation;
import com.AmanecerTropical.dto.RoomDTO;
import com.AmanecerTropical.entity.Flight;
import com.AmanecerTropical.entity.Hotel;
import com.AmanecerTropical.entity.Room;
import com.AmanecerTropical.service.ReservationService;
import com.AmanecerTropical.service.FlightService;
import com.AmanecerTropical.service.HotelService;
import com.AmanecerTropical.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private FlightService flightService;

    @Autowired
    private HotelService hotelService;

    @Autowired
    private RoomService roomService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Reservation>> getAllReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/user/{usuarioId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication, #usuarioId)")
    public ResponseEntity<List<Reservation>> getReservationsByUserId(@PathVariable Long usuarioId) {
        List<Reservation> reservations = reservationService.getReservationsByUser(usuarioId);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/package/{paqueteId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Reservation>> getReservationsByPackageId(@PathVariable Long paqueteId) {
        List<Reservation> reservations = reservationService.getReservationsByDestination(paqueteId);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @reservationSecurity.hasReservationAccess(authentication, #id)")
    public ResponseEntity<Reservation> getReservationById(@PathVariable @NonNull Long id) {
        Optional<Reservation> reservation = reservationService.getReservationById(id);
        return reservation.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('USUARIO') or hasRole('ADMIN')")
    public ResponseEntity<Reservation> createReservation(@Valid @RequestBody @NonNull Reservation reservation) {
        try {
            Reservation createdReservation = reservationService.createReservation(reservation);
            return ResponseEntity.ok(createdReservation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @reservationSecurity.hasReservationAccess(authentication, #id)")
    public ResponseEntity<Reservation> updateReservation(@PathVariable @NonNull Long id, @Valid @RequestBody @NonNull Reservation reservation) {
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
    public ResponseEntity<Boolean> checkPackageAvailability(@RequestParam Long paqueteId,
                                                           @RequestParam String fechaInicio,
                                                           @RequestParam String fechaFin) {
        LocalDate inicio = LocalDate.parse(fechaInicio);
        LocalDate fin = LocalDate.parse(fechaFin);
        boolean available = reservationService.isPackageAvailable(paqueteId, inicio, fin);
        return ResponseEntity.ok(available);
    }

    @GetMapping("/hotel/{hotelId}/available")
    public List<RoomDTO> getAvailableRoomsByHotelId(@PathVariable Long hotelId) {
        return roomService.getAvailableRoomsByHotel(hotelId);
    }

    @GetMapping("/overlapping")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Reservation>> findOverlappingReservations(@RequestParam(required = false) Long paqueteId,
                                                                        @RequestParam(required = false) Long vueloId,
                                                                        @RequestParam(required = false) Long hotelId,
                                                                        @RequestParam(required = false) Long vehiculoId,
                                                                        @RequestParam String fechaInicio,
                                                                        @RequestParam String fechaFin) {
        LocalDate inicio = LocalDate.parse(fechaInicio);
        LocalDate fin = LocalDate.parse(fechaFin);
        List<Reservation> overlappingReservations = reservationService.getOverlappingReservations(paqueteId, vueloId, hotelId, vehiculoId, inicio, fin);
        return ResponseEntity.ok(overlappingReservations);
    }

    @PostMapping("/validate-compatibility")
    @PreAuthorize("hasRole('USUARIO') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> validateServicesCompatibility(
            @RequestBody Map<String, List<Long>> serviceIds) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Long> flightIds = serviceIds.getOrDefault("vuelos", new ArrayList<>());
            List<Long> hotelIds = serviceIds.getOrDefault("hoteles", new ArrayList<>());
            List<Long> vehicleIds = serviceIds.getOrDefault("vehiculos", new ArrayList<>());
            
            String commonLocation = null;
            boolean compatible = true;
            
            if (!flightIds.isEmpty()) {
                @SuppressWarnings("null")
                Optional<Flight> flight = flightService.getFlightById(flightIds.get(0));
                if (flight.isPresent()) {
                    commonLocation = flight.get().getDestino();
                }
            }
            
            if (!hotelIds.isEmpty()) {
                for (Long hotelId : hotelIds) {
                    @SuppressWarnings("null")
                    Optional<Hotel> hotel = hotelService.getHotelById(hotelId);
                    if (hotel.isPresent()) {
                        String hotelLocation = hotel.get().getUbicacion();
                        if (commonLocation == null) {
                            commonLocation = hotelLocation;
                        } else if (!locationMatches(commonLocation, hotelLocation)) {
                            compatible = false;
                            response.put("reason", "Las ubicaciones de los servicios no coinciden");
                            break;
                        }
                    }
                }
            }
            
            response.put("compatible", compatible);
            response.put("location", commonLocation != null ? commonLocation : "No especificada");
            response.put("services", Map.of(
                "vuelos", flightIds.size(),
                "hoteles", hotelIds.size(),
                "vehiculos", vehicleIds.size()
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("compatible", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    private boolean locationMatches(String location1, String location2) {
        if (location1 == null || location2 == null) return false;
        
        String loc1 = location1.toLowerCase().trim();
        String loc2 = location2.toLowerCase().trim();
        
        if (loc1.equals(loc2)) return true;
        
        if (loc1.contains(loc2) || loc2.contains(loc1)) return true;
        
        return false;
    }

    @PostMapping("/batch")
    @PreAuthorize("hasRole('USUARIO') or hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<Map<String, Object>> createBatchReservations(
            @Valid @RequestBody List<Reservation> reservations) {
        
        Map<String, Object> response = new HashMap<>();
        List<Reservation> createdReservations = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        
        for (int i = 0; i < reservations.size(); i++) {
            try {
                Reservation reservation = reservations.get(i);
                @SuppressWarnings("null")
                Reservation created = reservationService.createReservation(reservation);
                createdReservations.add(created);
            } catch (Exception e) {
                errors.add("Reservación " + (i + 1) + ": " + e.getMessage());
            }
        }
        
        response.put("success", createdReservations.size());
        response.put("errors", errors.size());
        response.put("reservations", createdReservations);
        
        if (!errors.isEmpty()) {
            response.put("errorMessages", errors);
        }
        
        if (createdReservations.isEmpty()) {
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }
}