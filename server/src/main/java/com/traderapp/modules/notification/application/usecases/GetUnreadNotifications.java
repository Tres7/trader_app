package com.traderapp.modules.notification.application.usecases;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.traderapp.modules.notification.domain.entities.Notification;
import com.traderapp.modules.notification.domain.repositories.NotificationRepository;

@Service
public class GetUnreadNotifications {
    private final NotificationRepository notificationRepository;

    public GetUnreadNotifications(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<Notification> execute(UUID userId) {
        return notificationRepository.findUnreadByUserId(userId);
    }
}