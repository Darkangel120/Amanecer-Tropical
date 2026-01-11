package com.AmanecerTropical.repository;

import com.AmanecerTropical.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByReservacionId(Long reservacionId);

    List<Payment> findByEstadoPago(String estadoPago);

    List<Payment> findByMetodoPago(String metodoPago);

    List<Payment> findByReservacionUsuarioId(Long usuarioId);

    @Query("SELECT p FROM Payment p WHERE p.fechaPago BETWEEN :fechaInicio AND :fechaFin")
    List<Payment> findByFechaPagoBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                        @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT SUM(p.monto) FROM Payment p WHERE p.estadoPago = 'completado'")
    BigDecimal getTotalPagosCompletados();

    @Query("SELECT p FROM Payment p WHERE p.referenciaPago = :referenciaPago")
    Payment findByReferenciaPago(@Param("referenciaPago") String referenciaPago);

    List<Payment> findByFechaCreacionAfter(LocalDateTime fechaCreacion);
}
