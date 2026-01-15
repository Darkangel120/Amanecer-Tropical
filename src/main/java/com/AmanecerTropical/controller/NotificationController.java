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

    @GetMapping("/user/{usuarioId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication, #usuarioId)")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable Long usuarioId) {
        List<Notification> notifications = notificationService.getAllNotifications(usuarioId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/user/{usuarioId}/unread")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication, #usuarioId)")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable Long usuarioId) {
        List<Notification> notifications = notificationService.getUnreadNotifications(usuarioId);
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
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<User> userOptional = userService.getUserByEmail(email);
        if (userOptional.isPresent()) {
            Long usuarioId = userOptional.get().getId();
            if (usuarioId != null) {
                notificationService.markAllAsRead(usuarioId);
            }
        }
        return ResponseEntity.ok().build();
    }
}