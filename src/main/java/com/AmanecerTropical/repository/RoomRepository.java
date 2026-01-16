package com.AmanecerTropical.repository;

import com.AmanecerTropical.dto.RoomDTO;
import com.AmanecerTropical.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT new com.AmanecerTropical.dto.RoomDTO(r.id, r.hotel.id, r.numeroHabitacion, " +
           "r.tipoHabitacion, r.capacidad, r.precioPorNoche, r.comodidades, r.disponible, r.activo) " +
           "FROM Room r WHERE r.hotel.id = ?1 AND r.disponible = 1")
    List<RoomDTO> findByHotel_IdAndDisponibleTrue(Long hotelId);

    @Query(value = "SELECT MIN(precio_por_noche) FROM habitaciones WHERE hotel_id = ?1 AND disponible = 1", nativeQuery = true)
    BigDecimal findMinPriceByHotelId(Long hotelId);
}