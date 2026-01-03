package com.AmanecerTropical.controller;

import com.AmanecerTropical.entity.Review;
import com.AmanecerTropical.service.ReviewService;
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

    @GetMapping("/destination/{destinationId}")
    public ResponseEntity<List<Review>> getReviewsByDestination(@PathVariable @NonNull Long destinationId) {
        List<Review> reviews = reviewService.getReviewsByDestination(destinationId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/flight/{flightId}")
    public ResponseEntity<List<Review>> getReviewsByFlight(@PathVariable @NonNull Long flightId) {
        List<Review> reviews = reviewService.getReviewsByFlight(flightId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<Review>> getReviewsByHotel(@PathVariable @NonNull Long hotelId) {
        List<Review> reviews = reviewService.getReviewsByHotel(hotelId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<Review>> getReviewsByVehicle(@PathVariable @NonNull Long vehicleId) {
        List<Review> reviews = reviewService.getReviewsByVehicle(vehicleId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication, #userId)")
    public ResponseEntity<List<Review>> getReviewsByUser(@PathVariable @NonNull Long userId) {
        List<Review> reviews = reviewService.getReviewsByUser(userId);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Review> createReview(@RequestBody @NonNull Review review) {
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
