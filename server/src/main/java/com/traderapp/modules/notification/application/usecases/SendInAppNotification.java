package com.traderapp.modules.notification.application.usecases;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.traderapp.modules.notification.application.ports.output.SseNotificationSender;
import com.traderapp.modules.notification.domain.entities.Notification;
import com.traderapp.modules.notification.domain.repositories.NotificationRepository;

@Service
public class SendInAppNotification {
    private final SseNotificationSender sseNotificationSender;
    private final NotificationRepository notificationRepository;
    
    
    public SendInAppNotification(SseNotificationSender sseNotificationSender, NotificationRepository notificationRepository) {
        this.sseNotificationSender = sseNotificationSender;
        this.notificationRepository = notificationRepository;
    }

    public void execute(UUID userId, String type, String title, String message) {
        Notification notification = Notification.create(userId, type, title, message);
        notificationRepository.save(notification);
        sseNotificationSender.send(userId, type, title, message);
    }
}
