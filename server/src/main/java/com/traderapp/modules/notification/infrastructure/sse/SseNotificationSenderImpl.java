package com.traderapp.modules.notification.infrastructure.sse;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.traderapp.modules.notification.application.ports.output.SseNotificationSender;

@Component
public class SseNotificationSenderImpl implements SseNotificationSender {

    private static final Logger log = LoggerFactory.getLogger(SseNotificationSenderImpl.class);

    private final SseConnectionRegistry registry;

    public SseNotificationSenderImpl(SseConnectionRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void send(UUID userId, String type, String title, String message) {
        SseEmitter emitter = registry.get(userId);
        if (emitter == null) {
            return;
        }
        try {
            emitter.send(SseEmitter.event()
                .name(type)
                .data(new SseNotificationPayload(type, title, message)));
        } catch (IOException e) {
            log.warn("Failed to send SSE to user {}: {}", userId, e.getMessage());
            emitter.completeWithError(e);
        }
    }

    public record SseNotificationPayload(String type, String title, String message) {}
}
