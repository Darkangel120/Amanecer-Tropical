package com.AmanecerTropical.service;

import com.AmanecerTropical.entity.Notification;
import com.AmanecerTropical.entity.Review;
import com.AmanecerTropical.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private NotificationService notificationService;

    public List<Review> getReviewsByDestination(@NonNull Long paqueteId) {
        return reviewRepository.findByPaqueteId(paqueteId);
    }

    public List<Review> getReviewsByFlight(@NonNull Long vueloId) {
        return reviewRepository.findByVueloId(vueloId);
    }

    public List<Review> getReviewsByHotel(@NonNull Long hotelId) {
        return reviewRepository.findByHotelId(hotelId);
    }

    public List<Review> getReviewsByVehicle(@NonNull Long vehiculoId) {
        return reviewRepository.findByVehiculoId(vehiculoId);
    }

    public List<Review> getReviewsByUser(@NonNull Long usuarioId) {
        return reviewRepository.findByUsuarioId(usuarioId);
    }

    public Review saveReview(@NonNull Review review) {
        Review savedReview = reviewRepository.save(review);

        // Create notification for new review
        String servicioNombre = "";
        if (savedReview.getPaquete() != null) {
            servicioNombre = savedReview.getPaquete().getNombre();
        } else if (savedReview.getVuelo() != null) {
            servicioNombre = "vuelo " + savedReview.getVuelo().getFlightNumber();
        } else if (savedReview.getHotel() != null) {
            servicioNombre = savedReview.getHotel().getNombre();
        } else if (savedReview.getVehiculo() != null) {
            servicioNombre = savedReview.getVehiculo().getNombre();
        }

        Notification notification = new Notification(
            savedReview.getUsuario(),
            "Nueva Reseña",
            "Tu reseña para " + servicioNombre + " ha sido publicada.",
            "resena"
        );
        notificationService.saveNotification(notification);

        return savedReview;
    }

    public void deleteReview(@NonNull Long id) {
        reviewRepository.deleteById(id);
    }
    
    public Double getAverageRatingByService(String tipoServicio, Long servicioId) {
        List<Review> reviews;
        switch (tipoServicio) {
            case "paquete":
                reviews = reviewRepository.findByPaqueteId(servicioId);
                break;
            case "vuelo":
                reviews = reviewRepository.findByVueloId(servicioId);
                break;
            case "hotel":
                reviews = reviewRepository.findByHotelId(servicioId);
                break;
            case "vehiculo":
                reviews = reviewRepository.findByVehiculoId(servicioId);
                break;
            default:
                return 0.0;
        }
        
        if (reviews.isEmpty()) {
            return 0.0;
        }
        
        double sum = reviews.stream().mapToInt(Review::getCalificacion).sum();
        return sum / reviews.size();
    }

    public Optional<Review> getReviewById(Long reviewId) {
        throw new UnsupportedOperationException("Unimplemented method 'getReviewById'");
    }
}