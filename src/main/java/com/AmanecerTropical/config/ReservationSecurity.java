package com.AmanecerTropical.config;

import com.AmanecerTropical.entity.Reservation;
import com.AmanecerTropical.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("reservationSecurity")
public class ReservationSecurity {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserSecurity userSecurity;

    public boolean hasReservationAccess(Authentication authentication, Long reservationId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Optional<Reservation> reservation = reservationService.getReservationById(reservationId);
        if (reservation.isPresent()) {
            Long userId = reservation.get().getUsuario().getId();
            return userSecurity.hasUserId(authentication, userId) || userSecurity.isAdmin(authentication);
        }
        return false;
    }
}