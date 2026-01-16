package com.AmanecerTropical.repository;

import com.AmanecerTropical.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByVueloId(Long vueloId);

    List<Seat> findByVueloIdAndDisponible(Long vueloId, boolean disponible);

    @Query("SELECT s FROM Seat s WHERE s.vuelo.id = :vueloId AND s.disponible = true")
    List<Seat> findAvailableSeatsByVueloId(@Param("vueloId") Long vueloId);

    long countByVueloIdAndDisponible(Long vueloId, boolean disponible);
}
