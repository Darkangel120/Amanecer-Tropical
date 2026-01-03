package com.AmanecerTropical.service;

import com.AmanecerTropical.entity.Notification;
import com.AmanecerTropical.entity.Review;
import com.AmanecerTropical.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private NotificationService notificationService;

    public List<Review> getReviewsByDestination(@NonNull Long destinationId) {
        return reviewRepository.findByDestinationId(destinationId);
    }

    public List<Review> getReviewsByFlight(@NonNull Long flightId) {
        return reviewRepository.findByFlightId(flightId);
    }

    public List<Review> getReviewsByHotel(@NonNull Long hotelId) {
        return reviewRepository.findByHotelId(hotelId);
    }

    public List<Review> getReviewsByVehicle(@NonNull Long vehicleId) {
        return reviewRepository.findByVehicleId(vehicleId);
    }

    public List<Review> getReviewsByUser(@NonNull Long userId) {
        return reviewRepository.findByUserId(userId);
    }

    public Review saveReview(@NonNull Review review) {
        Review savedReview = reviewRepository.save(review);

        // Create notification for new review
        Notification notification = new Notification(
            savedReview.getUser(),
            "Nueva Reseña",
            "Tu reseña para " + (savedReview.getDestination() != null ? savedReview.getDestination().getName() :
                                savedReview.getFlight() != null ? savedReview.getFlight().getFlightNumber() :
                                savedReview.getHotel() != null ? savedReview.getHotel().getName() :
                                savedReview.getVehicle().getName()) + " ha sido publicada.",
            "review"
        );
        notificationService.saveNotification(notification);

        return savedReview;
    }

    public void deleteReview(@NonNull Long id) {
        reviewRepository.deleteById(id);
    }
}
