package com.AmanecerTropical.repository;

import com.AmanecerTropical.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r LEFT JOIN FETCH r.destination LEFT JOIN FETCH r.flight LEFT JOIN FETCH r.hotel LEFT JOIN FETCH r.vehicle WHERE r.user.id = :userId")
    List<Reservation> findByUserId(@Param("userId") Long userId);

    List<Reservation> findByDestinationId(Long destinationId);

    List<Reservation> findByStatus(String status);

    List<Reservation> findByUserIdAndStatus(Long userId, String status);

    @Query("SELECT r FROM Reservation r WHERE " +
           "(:destinationId IS NULL OR r.destination.id = :destinationId) AND " +
           "(:flightId IS NULL OR r.flight.id = :flightId) AND " +
           "(:hotelId IS NULL OR r.hotel.id = :hotelId) AND " +
           "(:vehicleId IS NULL OR r.vehicle.id = :vehicleId) AND " +
           "r.status != 'cancelled' AND ((r.startDate <= :endDate AND r.endDate >= :startDate))")
    List<Reservation> findOverlappingReservations(@Param("destinationId") Long destinationId,
                                                 @Param("flightId") Long flightId,
                                                 @Param("hotelId") Long hotelId,
                                                 @Param("vehicleId") Long vehicleId,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE " +
           "(:destinationId IS NULL OR r.destination.id = :destinationId) AND " +
           "(:flightId IS NULL OR r.flight.id = :flightId) AND " +
           "(:hotelId IS NULL OR r.hotel.id = :hotelId) AND " +
           "(:vehicleId IS NULL OR r.vehicle.id = :vehicleId) AND " +
           "r.status != 'cancelled' AND ((r.startDate <= :endDate AND r.endDate >= :startDate))")
    long countOverlappingReservations(@Param("destinationId") Long destinationId,
                                     @Param("flightId") Long flightId,
                                     @Param("hotelId") Long hotelId,
                                     @Param("vehicleId") Long vehicleId,
                                     @Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);

    List<Reservation> findByStartDateBetween(LocalDate start, LocalDate end);

    @Query("SELECT r FROM Reservation r WHERE r.createdAt >= :startDate ORDER BY r.createdAt DESC")
    List<Reservation> findRecentReservations(@Param("startDate") LocalDate startDate);
}
