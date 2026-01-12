package com.AmanecerTropical.controller;

import com.AmanecerTropical.entity.Payment;
import com.AmanecerTropical.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
        try {
            // Validate required fields
            if (!paymentData.containsKey("monto") || paymentData.get("monto") == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "El monto del pago es requerido"
                ));
            }

            if (!paymentData.containsKey("metodoPago") || paymentData.get("metodoPago") == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "El método de pago es requerido"
                ));
            }

            // Validate reservationIds
            if (!paymentData.containsKey("reservationIds") || paymentData.get("reservationIds") == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Los IDs de reservación son requeridos"
                ));
            }

            // Validate payment method
            String metodoPago = paymentData.get("metodoPago").toString();
            List<String> validMethods = List.of("tarjeta_credito", "tarjeta_debito", "transferencia", "pagomovil", "efectivo");
            if (!validMethods.contains(metodoPago)) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Método de pago no válido"
                ));
            }

            // Validate amount
            BigDecimal monto;
            try {
                monto = new BigDecimal(paymentData.get("monto").toString());
                if (monto.compareTo(BigDecimal.ZERO) <= 0) {
                    return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "El monto debe ser mayor a cero"
                    ));
                }
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "El monto debe ser un número válido"
                ));
            }

            // Get reservation IDs
            List<?> reservationIdsObj = (List<?>) paymentData.get("reservationIds");
            List<Long> reservationIds = reservationIdsObj.stream()
                .map(obj -> Long.valueOf(obj.toString()))
                .toList();

            if (reservationIds.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Debe proporcionar al menos un ID de reservación"
                ));
            }

            // Create payments for each reservation
            List<Long> paymentIds = new java.util.ArrayList<>();
            for (Long reservationId : reservationIds) {
                Payment payment = new Payment();
                payment.setMonto(monto);
                payment.setMetodoPago(metodoPago);
                payment.setReferenciaPago((String) paymentData.get("referenciaPago"));
                payment.setEstadoPago("sin confirmacion");

                // Set reservation reference
                com.AmanecerTropical.entity.Reservation reservation = new com.AmanecerTropical.entity.Reservation();
                reservation.setId(reservationId);
                payment.setReservacion(reservation);

                // Save payment to database
                Payment savedPayment = paymentService.createPayment(payment);
                paymentIds.add(savedPayment.getId());
            }

            Map<String, Object> response = Map.of(
                "success", true,
                "message", "Pago procesado exitosamente",
                "transactionId", "TXN-" + String.join("-", paymentIds.stream().map(String::valueOf).toList()),
                "paymentIds", paymentIds,
                "reservationIds", reservationIds
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = Map.of(
                "success", false,
                "message", "Error al procesar el pago: " + e.getMessage()
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
