package com.AmanecerTropical.service;

import com.AmanecerTropical.entity.Notification;
import com.AmanecerTropical.repository.NotificationRepository;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public List<Notification> getUnreadNotifications(Long usuarioId) {
        return notificationRepository.findByUsuarioIdAndLeidoFalse(usuarioId);
    }

    public List<Notification> getAllNotifications(Long usuarioId) {
        return notificationRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId);
    }

    public @NonNull Notification saveNotification(@NonNull Notification notification) {
        return notificationRepository.save(notification);
    }

    public void markAsRead(@NonNull Long id) {
        Optional<Notification> optionalNotification = notificationRepository.findById(id);
        if (optionalNotification.isPresent()) {
            Notification notification = optionalNotification.get();
            notification.setLeido(true);
            notificationRepository.save(notification);
        }
    }

    public void deleteNotification(@NonNull Long id) {
        notificationRepository.deleteById(id);
    }

    public void markAllAsRead(@NonNull Long usuarioId) {
        List<Notification> unreadNotifications = notificationRepository.findByUsuarioIdAndLeidoFalse(usuarioId);
        unreadNotifications.forEach(notification -> notification.setLeido(true));
        notificationRepository.saveAll(unreadNotifications);
    }
    
    public List<Notification> getNotificationsByType(Long usuarioId, String tipo) {
        return notificationRepository.findByUsuarioIdAndTipo(usuarioId, tipo);
    }

    public Optional<Notification> getNotificationById(@NonNull Long id) {
        return notificationRepository.findById(id);
    }
}