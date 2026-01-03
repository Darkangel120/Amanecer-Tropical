package com.AmanecerTropical.repository;

import com.AmanecerTropical.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByDestinationId(Long destinationId);
    List<Review> findByFlightId(Long flightId);
    List<Review> findByHotelId(Long hotelId);
    List<Review> findByVehicleId(Long vehicleId);
    List<Review> findByUserId(Long userId);
}
