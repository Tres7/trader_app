package com.traderapp.modules.notification.domain.repositories;

import java.util.List;
import java.util.UUID;

import com.traderapp.modules.notification.domain.entities.Notification;

public interface NotificationRepository {
    Notification save (Notification notification);
    List<Notification> findUnreadByUserId(UUID userId);
    void markAsRead(UUID notificationId);
}
