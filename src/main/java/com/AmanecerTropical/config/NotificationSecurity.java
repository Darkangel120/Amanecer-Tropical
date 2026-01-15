package com.AmanecerTropical.config;

import com.AmanecerTropical.entity.Notification;
import com.AmanecerTropical.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("notificationSecurity")
public class NotificationSecurity {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserSecurity userSecurity;

    public boolean hasNotificationAccess(Authentication authentication, Long notificationId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Optional<Notification> notification = notificationService.getNotificationById(notificationId);
        if (notification.isPresent()) {
            Long userId = notification.get().getUsuario().getId();
            return userSecurity.hasUserId(authentication, userId) || userSecurity.isAdmin(authentication);
        }
        return false;
    }
}