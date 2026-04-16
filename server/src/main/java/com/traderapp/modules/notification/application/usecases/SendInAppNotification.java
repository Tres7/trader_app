package com.traderapp.modules.notification.application.usecases;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.traderapp.modules.notification.application.ports.output.SseNotificationSender;

@Service
public class SendInAppNotification {
    private final SseNotificationSender sseNotificationSender;

    public SendInAppNotification(SseNotificationSender sseNotificationSender) {
        this.sseNotificationSender = sseNotificationSender;
    }

    public void execute(UUID userId, String type, String title, String message) {
        sseNotificationSender.send(userId, type, title, message);
    }
}
