package com.AmanecerTropical.service;

import com.AmanecerTropical.entity.Payment;
import com.AmanecerTropical.entity.Reservation;
import com.AmanecerTropical.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @SuppressWarnings("null")
    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    public List<Payment> getPaymentsByReservationId(Long reservacionId) {
        return paymentRepository.findByReservacionId(reservacionId);
    }

    public List<Payment> getPaymentsByUserId(Long usuarioId) {
        return paymentRepository.findByReservacionUsuarioId(usuarioId);
    }

    public List<Payment> getPaymentsByStatus(String estadoPago) {
        return paymentRepository.findByEstadoPago(estadoPago);
    }

    @Transactional
    public Payment createPayment(Payment payment) {
        // Generar referencia de pago única
        if (payment.getReferenciaPago() == null || payment.getReferenciaPago().isEmpty()) {
            payment.setReferenciaPago(generatePaymentReference());
        }
        return paymentRepository.save(payment);
    }

    @SuppressWarnings("null")
    @Transactional
    public Payment updatePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    @SuppressWarnings("null")
    @Transactional
    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }

    @Transactional
    public Payment processPayment(Reservation reservation, BigDecimal monto, String metodoPago, String datosPago) {
        Payment payment = new Payment();
        payment.setReservacion(reservation);
        payment.setMonto(monto);
        payment.setMetodoPago(metodoPago);
        payment.setEstadoPago("completado"); // En un sistema real, esto vendría de un procesador de pagos

        return createPayment(payment);
    }

    @Transactional
    public Payment createPendingPayment(Reservation reservation, BigDecimal monto, String metodoPago) {
        Payment payment = new Payment();
        payment.setReservacion(reservation);
        payment.setMonto(monto);
        payment.setMetodoPago(metodoPago);
        payment.setEstadoPago("pendiente");

        return createPayment(payment);
    }

    public boolean validatePaymentAmount(BigDecimal expectedAmount, BigDecimal paidAmount) {
        return expectedAmount.compareTo(paidAmount) == 0;
    }

    private String generatePaymentReference() {
        return "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
