package com.traderapp.modules.notification.infrastructure.http.controllers;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.traderapp.modules.auth.application.dto.AuthenticatedUser;
import com.traderapp.modules.notification.application.ports.output.SseNotificationSender;
import com.traderapp.modules.notification.application.usecases.GetUnreadNotifications;
import com.traderapp.modules.notification.application.usecases.MarkNotificationAsRead;
import com.traderapp.modules.notification.domain.entities.Notification;
import com.traderapp.modules.notification.infrastructure.http.responses.NotificationResponse;
import com.traderapp.modules.notification.infrastructure.sse.SseConnectionRegistry;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationSseController {

    private static final Logger log = LoggerFactory.getLogger(NotificationSseController.class);

    private final SseConnectionRegistry registry;
    private final GetUnreadNotifications getUnreadNotifications;
    private final SseNotificationSender sseNotificationSender;
    private final MarkNotificationAsRead markNotificationAsRead;

    public NotificationSseController(
        SseConnectionRegistry registry,
        GetUnreadNotifications getUnreadNotifications,
        SseNotificationSender sseNotificationSender,
        MarkNotificationAsRead markNotificationAsRead
    ) {
        this.registry = registry;
        this.getUnreadNotifications = getUnreadNotifications;
        this.sseNotificationSender = sseNotificationSender;
        this.markNotificationAsRead = markNotificationAsRead;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) throws IOException {
        UUID userId = UUID.fromString(authenticatedUser.userId());
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        registry.register(userId, emitter);

        try {
            emitter.send(SseEmitter.event().comment("connected"));
        } catch (IOException e) {
            log.warn("Failed to send initial SSE event: {}", e.getMessage());
        }

        List<Notification> unread = getUnreadNotifications.execute(userId);
        for (Notification notification : unread) {
            sseNotificationSender.send(
                userId,
                notification.getType(),
                notification.getTitle(),
                notification.getMessage()
            );
        }

        return emitter;
    }

    @GetMapping("/unread")
    public List<NotificationResponse> getUnread(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        UUID userId = UUID.fromString(authenticatedUser.userId());
        return getUnreadNotifications.execute(userId)
            .stream()
            .map(NotificationResponse::from)
            .toList();
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID id) {
        markNotificationAsRead.execute(id);
        return ResponseEntity.noContent().build();
    }
}