package com.AmanecerTropical.repository;

import com.AmanecerTropical.entity.Destination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DestinationRepository extends JpaRepository<Destination, Long> {

    List<Destination> findByActiveTrue();

    List<Destination> findByCategory(String category);

    List<Destination> findByActiveTrueAndCategory(String category);

    @Query("SELECT d FROM Destination d WHERE d.active = true AND LOWER(d.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(d.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(d.location) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Destination> searchByKeyword(@Param("keyword") String keyword);

    List<Destination> findByActiveTrueOrderByNameAsc();

    // Additional methods to match service calls
    List<Destination> findByNameContainingIgnoreCase(String name);
    List<Destination> findByLocationContainingIgnoreCase(String location);
    List<Destination> findByPriceBetween(double minPrice, double maxPrice);
    List<Destination> findByAvailable(boolean available);
}
