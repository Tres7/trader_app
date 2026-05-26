package com.traderapp.modules.notification.infrastructure.sse;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class SseConnectionRegistry {
    private final Map<UUID, SseEmitter> emitters = new ConcurrentHashMap<>();

    public void register(UUID userId, SseEmitter emitter) {
        emitters.put(userId, emitter);
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError(e -> emitters.remove(userId));
    }

    public SseEmitter get(UUID userId) {
        return emitters.get(userId);
    }

    public boolean isConnected(UUID userId) {
        return emitters.containsKey(userId);
    }
}
