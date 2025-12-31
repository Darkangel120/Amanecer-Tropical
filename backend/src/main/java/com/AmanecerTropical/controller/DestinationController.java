package com.AmanecerTropical.controller;

import com.AmanecerTropical.entity.Destination;
import com.AmanecerTropical.service.DestinationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/destinations")
public class DestinationController {

    @Autowired
    private DestinationService destinationService;

    @GetMapping
    public ResponseEntity<List<Destination>> getAllDestinations() {
        List<Destination> destinations = destinationService.getAllDestinations();
        return ResponseEntity.ok(destinations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Destination> getDestinationById(@PathVariable Long id) {
        Optional<Destination> destination = destinationService.getDestinationById(id);
        return destination.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Destination>> searchDestinations(@RequestParam(required = false) String name,
                                                                @RequestParam(required = false) String location) {
        List<Destination> destinations;
        if (name != null && !name.isEmpty()) {
            destinations = destinationService.getDestinationsByName(name);
        } else if (location != null && !location.isEmpty()) {
            destinations = destinationService.getDestinationsByLocation(location);
        } else {
            destinations = destinationService.getAllDestinations();
        }
        return ResponseEntity.ok(destinations);
    }

    @GetMapping("/price-range")
    public ResponseEntity<List<Destination>> getDestinationsByPriceRange(@RequestParam double minPrice,
                                                                        @RequestParam double maxPrice) {
        List<Destination> destinations = destinationService.getDestinationsByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(destinations);
    }

    @GetMapping("/available")
    public ResponseEntity<List<Destination>> getAvailableDestinations() {
        List<Destination> destinations = destinationService.getAvailableDestinations();
        return ResponseEntity.ok(destinations);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Destination>> getDestinationsByCategory(@PathVariable String category) {
        List<Destination> destinations = destinationService.getDestinationsByCategory(category);
        return ResponseEntity.ok(destinations);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Destination> createDestination(@RequestBody Destination destination) {
        Destination createdDestination = destinationService.createDestination(destination);
        return ResponseEntity.ok(createdDestination);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Destination> updateDestination(@PathVariable Long id, @RequestBody Destination destination) {
        Optional<Destination> existingDestination = destinationService.getDestinationById(id);
        if (existingDestination.isPresent()) {
            destination.setId(id);
            Destination updatedDestination = destinationService.updateDestination(destination);
            return ResponseEntity.ok(updatedDestination);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDestination(@PathVariable Long id) {
        Optional<Destination> destination = destinationService.getDestinationById(id);
        if (destination.isPresent()) {
            destinationService.deleteDestination(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
