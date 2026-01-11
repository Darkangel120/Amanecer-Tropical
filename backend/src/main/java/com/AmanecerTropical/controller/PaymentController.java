package com.AmanecerTropical.controller;

import com.AmanecerTropical.entity.Payment;
import com.AmanecerTropical.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @paymentSecurity.hasPaymentAccess(authentication, #id)")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        Optional<Payment> payment = paymentService.getPaymentById(id);
        return payment.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/reservation/{reservacionId}")
    @PreAuthorize("hasRole('ADMIN') or @reservationSecurity.hasReservationAccess(authentication, #reservacionId)")
    public ResponseEntity<List<Payment>> getPaymentsByReservationId(@PathVariable Long reservacionId) {
        List<Payment> payments = paymentService.getPaymentsByReservationId(reservacionId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/user/{usuarioId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication, #usuarioId)")
    public ResponseEntity<List<Payment>> getPaymentsByUserId(@PathVariable Long usuarioId) {
        List<Payment> payments = paymentService.getPaymentsByUserId(usuarioId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/status/{estadoPago}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Payment>> getPaymentsByStatus(@PathVariable String estadoPago) {
        List<Payment> payments = paymentService.getPaymentsByStatus(estadoPago);
        return ResponseEntity.ok(payments);
    }

    @PostMapping
    @PreAuthorize("hasRole('USUARIO') or hasRole('ADMIN')")
    public ResponseEntity<Payment> createPayment(@Valid @RequestBody Payment payment) {
        try {
            Payment createdPayment = paymentService.createPayment(payment);
            return ResponseEntity.ok(createdPayment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @paymentSecurity.hasPaymentAccess(authentication, #id)")
    public ResponseEntity<Payment> updatePayment(@PathVariable Long id, @Valid @RequestBody Payment payment) {
        Optional<Payment> existingPayment = paymentService.getPaymentById(id);
        if (existingPayment.isPresent()) {
            payment.setId(id);
            Payment updatedPayment = paymentService.updatePayment(payment);
            return ResponseEntity.ok(updatedPayment);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @paymentSecurity.hasPaymentAccess(authentication, #id)")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        Optional<Payment> payment = paymentService.getPaymentById(id);
        if (payment.isPresent()) {
            paymentService.deletePayment(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/process")
    @PreAuthorize("hasRole('USUARIO') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> processPayment(@RequestBody Map<String, Object> paymentData) {
        // This endpoint would integrate with a real payment processor
        // For now, it simulates payment processing
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Pago procesado exitosamente",
            "transactionId", "TXN-" + System.currentTimeMillis()
        );
        return ResponseEntity.ok(response);
    }
}
