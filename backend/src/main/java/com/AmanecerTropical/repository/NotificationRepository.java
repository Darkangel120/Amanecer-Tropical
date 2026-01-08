package com.AmanecerTropical.repository;

import com.AmanecerTropical.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    @Query("SELECT n FROM Notification n WHERE n.usuario.id = :usuarioId AND n.leido = false")
    List<Notification> findByUsuarioIdAndLeidoFalse(@Param("usuarioId") Long usuarioId);
    
    @Query("SELECT n FROM Notification n WHERE n.usuario.id = :usuarioId ORDER BY n.fechaCreacion DESC")
    List<Notification> findByUsuarioIdOrderByFechaCreacionDesc(@Param("usuarioId") Long usuarioId);
    
    @Query("SELECT n FROM Notification n WHERE n.usuario.id = :usuarioId AND n.tipo = :tipo")
    List<Notification> findByUsuarioIdAndTipo(@Param("usuarioId") Long usuarioId, @Param("tipo") String tipo);
}