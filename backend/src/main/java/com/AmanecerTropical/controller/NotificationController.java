package com.AmanecerTropical.controller;

import com.AmanecerTropical.entity.Notification;
import com.AmanecerTropical.entity.User;
import com.AmanecerTropical.service.NotificationService;
import com.AmanecerTropical.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication, #userId)")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getAllNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/user/{userId}/unread")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication, #userId)")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Notification> createNotification(@RequestBody @NonNull Notification notification) {
        Notification createdNotification = notificationService.saveNotification(notification);
        return ResponseEntity.ok(createdNotification);
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("hasRole('ADMIN') or @notificationSecurity.hasNotificationAccess(authentication, #id)")
    public ResponseEntity<Void> markAsRead(@PathVariable @NonNull Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @notificationSecurity.hasNotificationAccess(authentication, #id)")
    public ResponseEntity<Void> deleteNotification(@PathVariable @NonNull Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/mark-all-read")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> markAllAsRead() {
        // Get current user from security context
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        // Get user by email
        Optional<User> userOptional = userService.getUserByEmail(email);
        if (userOptional.isPresent()) {
            Long userId = userOptional.get().getId();
            if (userId != null) {
                notificationService.markAllAsRead(userId);
            }
        }
        return ResponseEntity.ok().build();
    }
}
