package com.AmanecerTropical.repository;

import com.AmanecerTropical.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    @Query("SELECT h FROM Hotel h WHERE h.active = true")
    List<Hotel> findByActiveTrue();
    List<Hotel> findByLocation(String location);
}
