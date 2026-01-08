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

    @Query("SELECT r FROM Reservation r LEFT JOIN FETCH r.paquete LEFT JOIN FETCH r.vuelo LEFT JOIN FETCH r.hotel LEFT JOIN FETCH r.vehiculo WHERE r.usuario.id = :usuarioId")
    List<Reservation> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    List<Reservation> findByPaqueteId(Long paqueteId);

    List<Reservation> findByEstado(String estado);

    List<Reservation> findByUsuarioIdAndEstado(Long usuarioId, String estado);

    @Query("SELECT r FROM Reservation r WHERE " +
           "(:paqueteId IS NULL OR r.paquete.id = :paqueteId) AND " +
           "(:vueloId IS NULL OR r.vuelo.id = :vueloId) AND " +
           "(:hotelId IS NULL OR r.hotel.id = :hotelId) AND " +
           "(:vehiculoId IS NULL OR r.vehiculo.id = :vehiculoId) AND " +
           "r.estado != 'cancelado' AND ((r.fechaInicio <= :fechaFin AND r.fechaFin >= :fechaInicio))")
    List<Reservation> findOverlappingReservations(@Param("paqueteId") Long paqueteId,
                                                 @Param("vueloId") Long vueloId,
                                                 @Param("hotelId") Long hotelId,
                                                 @Param("vehiculoId") Long vehiculoId,
                                                 @Param("fechaInicio") LocalDate fechaInicio,
                                                 @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE " +
           "(:paqueteId IS NULL OR r.paquete.id = :paqueteId) AND " +
           "(:vueloId IS NULL OR r.vuelo.id = :vueloId) AND " +
           "(:hotelId IS NULL OR r.hotel.id = :hotelId) AND " +
           "(:vehiculoId IS NULL OR r.vehiculo.id = :vehiculoId) AND " +
           "r.estado != 'cancelado' AND ((r.fechaInicio <= :fechaFin AND r.fechaFin >= :fechaInicio))")
    long countOverlappingReservations(@Param("paqueteId") Long paqueteId,
                                     @Param("vueloId") Long vueloId,
                                     @Param("hotelId") Long hotelId,
                                     @Param("vehiculoId") Long vehiculoId,
                                     @Param("fechaInicio") LocalDate fechaInicio,
                                     @Param("fechaFin") LocalDate fechaFin);

    List<Reservation> findByFechaInicioBetween(LocalDate start, LocalDate end);

    @Query("SELECT r FROM Reservation r WHERE r.fechaCreacion >= :fechaInicio ORDER BY r.fechaCreacion DESC")
    List<Reservation> findRecentReservations(@Param("fechaInicio") LocalDate fechaInicio);
}