package com.AmanecerTropical.controller;

import com.AmanecerTropical.entity.Review;
import com.AmanecerTropical.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/package/{paqueteId}")
    public ResponseEntity<List<Review>> getReviewsByPackage(@PathVariable @NonNull Long paqueteId) {
        List<Review> reviews = reviewService.getReviewsByDestination(paqueteId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/flight/{vueloId}")
    public ResponseEntity<List<Review>> getReviewsByFlight(@PathVariable @NonNull Long vueloId) {
        List<Review> reviews = reviewService.getReviewsByFlight(vueloId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<Review>> getReviewsByHotel(@PathVariable @NonNull Long hotelId) {
        List<Review> reviews = reviewService.getReviewsByHotel(hotelId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/vehicle/{vehiculoId}")
    public ResponseEntity<List<Review>> getReviewsByVehicle(@PathVariable @NonNull Long vehiculoId) {
        List<Review> reviews = reviewService.getReviewsByVehicle(vehiculoId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/user/{usuarioId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication, #usuarioId)")
    public ResponseEntity<List<Review>> getReviewsByUser(@PathVariable @NonNull Long usuarioId) {
        List<Review> reviews = reviewService.getReviewsByUser(usuarioId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/average")
    public ResponseEntity<Double> getAverageRating(@RequestParam String tipoServicio,
                                                   @RequestParam Long servicioId) {
        Double average = reviewService.getAverageRatingByService(tipoServicio, servicioId);
        return ResponseEntity.ok(average);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Review> createReview(@Valid @RequestBody @NonNull Review review) {
        Review createdReview = reviewService.saveReview(review);
        return ResponseEntity.ok(createdReview);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @reviewSecurity.hasReviewAccess(authentication, #id)")
    public ResponseEntity<Void> deleteReview(@PathVariable @NonNull Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}