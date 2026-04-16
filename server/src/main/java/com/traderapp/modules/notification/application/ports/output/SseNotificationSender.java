package com.traderapp.modules.notification.application.ports.output;

import java.util.UUID;

public interface SseNotificationSender {
    void send(UUID userId, String type, String title, String message);
}
