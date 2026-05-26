package com.traderapp.modules.notification.domain.entities;

import java.time.LocalDateTime;
import java.util.UUID;

public class Notification {
    private final UUID id;
    private final UUID userId;
    private final String type;
    private final String title;
    private final String message;
    private boolean read;
    private final LocalDateTime createdAt;
    
    public Notification(UUID id, UUID userId, String type, String title, String message, boolean read, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.message = message;
        this.read = read;
        this.createdAt = createdAt;
    }

    public static Notification create (UUID userId, String type, String title, String message) {
        return new Notification(userId, userId, type, title, message, false, LocalDateTime.now());
    }

    public UUID getId() { 
        return id; 
    }

    public UUID getUserId() { 
        return userId; 
    }

    public String getType() { 
        return type; 
    }

    public String getTitle() { 
        return title; 
    }

    public String getMessage() { 
        return message; 
    }

    public boolean isRead() { 
        return read;
    }

    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }
}
