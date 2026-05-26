package com.traderapp.modules.notification.infrastructure.persistence.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "notifications")
public class NotificationEntity {
    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "read", nullable = false)
    private boolean read;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected NotificationEntity() {}

    public NotificationEntity(UUID id, UUID userId, String type, String title, String message, boolean read, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.message = message;
        this.read = read;
        this.createdAt = createdAt;
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

    public void setRead(boolean read) { 
        this.read = read; 
    }
}
