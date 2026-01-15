package com.AmanecerTropical.controller;

import com.AmanecerTropical.entity.Hotel;
import com.AmanecerTropical.service.HotelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    @Autowired
    private HotelService hotelService;

    @GetMapping
    public ResponseEntity<List<Hotel>> getAllActiveHotels() {
        List<Hotel> hotels = hotelService.getAllActiveHotels();
        return ResponseEntity.ok(hotels);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Hotel>> searchHotels(@RequestParam @NonNull String ubicacion) {
        List<Hotel> hotels = hotelService.searchHotelsByUbicacion(ubicacion);
        return ResponseEntity.ok(hotels);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Hotel> getHotelById(@PathVariable @NonNull Long id) {
        Optional<Hotel> hotel = hotelService.getHotelById(id);
        return hotel.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Hotel> createHotel(@Valid @RequestBody @NonNull Hotel hotel) {
        Hotel createdHotel = hotelService.saveHotel(hotel);
        return ResponseEntity.ok(createdHotel);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Hotel> updateHotel(@PathVariable Long id, @RequestBody Hotel hotel) {
        hotel.setId(id);
        Hotel updatedHotel = hotelService.saveHotel(hotel);
        return ResponseEntity.ok(updatedHotel);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteHotel(@PathVariable @NonNull Long id) {
        hotelService.deleteHotel(id);
        return ResponseEntity.noContent().build();
    }
}