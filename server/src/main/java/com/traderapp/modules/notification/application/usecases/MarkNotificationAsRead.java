package com.traderapp.modules.notification.application.usecases;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.traderapp.modules.notification.domain.repositories.NotificationRepository;

@Service
public class MarkNotificationAsRead {
    private final NotificationRepository notificationRepository;

    public MarkNotificationAsRead(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void execute(UUID notificationId) {
        notificationRepository.markAsRead(notificationId);
    }
}