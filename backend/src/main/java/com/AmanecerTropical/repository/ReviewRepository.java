package com.AmanecerTropical.repository;

import com.AmanecerTropical.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByPaqueteId(Long paqueteId);
    List<Review> findByVueloId(Long vueloId);
    List<Review> findByHotelId(Long hotelId);
    List<Review> findByVehiculoId(Long vehiculoId);
    List<Review> findByUsuarioId(Long usuarioId);
    
    List<Review> findByTipoServicio(String tipoServicio);
}