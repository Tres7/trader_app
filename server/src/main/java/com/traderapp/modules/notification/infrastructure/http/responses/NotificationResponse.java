package com.traderapp.modules.notification.infrastructure.http.responses;

import java.time.LocalDateTime;
import java.util.UUID;

import com.traderapp.modules.notification.domain.entities.Notification;

public record NotificationResponse(
        UUID id,
        String type,
        String title,
        String message,
        LocalDateTime createdAt
) {
    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
            notification.getId(),
            notification.getType(),
            notification.getTitle(),
            notification.getMessage(),
            notification.getCreatedAt()
        );
    }
}