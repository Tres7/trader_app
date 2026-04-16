package com.traderapp.modules.notification.infrastructure.web;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.traderapp.modules.auth.application.dto.AuthenticatedUser;
import com.traderapp.modules.notification.infrastructure.sse.SseConnectionRegistry;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationSseController {

    private final SseConnectionRegistry registry;
    private static final Logger log = LoggerFactory.getLogger(NotificationSseController.class);

    public NotificationSseController(SseConnectionRegistry registry) {
        this.registry = registry;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) throws java.io.IOException {
        UUID userId = UUID.fromString(authenticatedUser.userId());
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        registry.register(userId, emitter);
        try {
            emitter.send(SseEmitter.event().comment("connected"));
        } catch (IOException e) {
            log.warn("Failed to send initial SSE event: {}", e.getMessage());
        }
        return emitter;
    }

}