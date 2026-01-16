package com.AmanecerTropical.controller;

import com.AmanecerTropical.entity.Seat;
import com.AmanecerTropical.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    @Autowired
    private SeatService seatService;

    @GetMapping
    public List<Seat> getAllSeats() {
        return seatService.getAllSeats();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Seat> getSeatById(@PathVariable Long id) {
        Optional<Seat> seat = seatService.getSeatById(id);
        return seat.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/vuelo/{vueloId}")
    public List<Seat> getSeatsByVueloId(@PathVariable Long vueloId) {
        return seatService.getSeatsByVueloId(vueloId);
    }

    @GetMapping("/vuelo/{vueloId}/available")
    public List<Seat> getAvailableSeatsByVueloId(@PathVariable Long vueloId) {
        return seatService.getAvailableSeatsByVueloId(vueloId);
    }

    @PostMapping
    public Seat createSeat(@RequestBody Seat seat) {
        return seatService.saveSeat(seat);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Seat> updateSeat(@PathVariable Long id, @RequestBody Seat seatDetails) {
        Optional<Seat> seat = seatService.getSeatById(id);
        if (seat.isPresent()) {
            Seat updatedSeat = seat.get();
            updatedSeat.setSeatNumber(seatDetails.getSeatNumber());
            updatedSeat.setClase(seatDetails.getClase());
            updatedSeat.setDisponible(seatDetails.isDisponible());
            updatedSeat.setActivo(seatDetails.isActivo());
            return ResponseEntity.ok(seatService.saveSeat(updatedSeat));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeat(@PathVariable Long id) {
        if (seatService.getSeatById(id).isPresent()) {
            seatService.deleteSeat(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/vuelo/{vueloId}/count-available")
    public long countAvailableSeatsByVueloId(@PathVariable Long vueloId) {
        return seatService.countAvailableSeatsByVueloId(vueloId);
    }
}
