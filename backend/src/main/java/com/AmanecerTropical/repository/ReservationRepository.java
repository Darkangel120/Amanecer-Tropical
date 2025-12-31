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

    List<Reservation> findByUserId(Long userId);

    List<Reservation> findByDestinationId(Long destinationId);

    List<Reservation> findByStatus(String status);

    List<Reservation> findByUserIdAndStatus(Long userId, String status);

    @Query("SELECT r FROM Reservation r WHERE r.destination.id = :destinationId AND r.status != 'cancelled' AND ((r.startDate <= :endDate AND r.endDate >= :startDate))")
    List<Reservation> findOverlappingReservations(@Param("destinationId") Long destinationId,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.destination.id = :destinationId AND r.status != 'cancelled' AND ((r.startDate <= :endDate AND r.endDate >= :startDate))")
    long countOverlappingReservations(@Param("destinationId") Long destinationId,
                                     @Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);

    List<Reservation> findByStartDateBetween(LocalDate start, LocalDate end);

    @Query("SELECT r FROM Reservation r WHERE r.createdAt >= :startDate ORDER BY r.createdAt DESC")
    List<Reservation> findRecentReservations(@Param("startDate") LocalDate startDate);
}
