package com.traderapp.modules.auth.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "email_verification_codes")
public class EmailVerificationCodeEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "code", nullable = false, length = 6)
    private String code;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "used", nullable = false)
    private boolean used;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    protected EmailVerificationCodeEntity() {
    }

    public EmailVerificationCodeEntity(
            UUID id,
            UUID userId,
            String code,
            LocalDateTime expiresAt,
            LocalDateTime createdAt,
            boolean used,
            LocalDateTime usedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.code = code;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
        this.used = used;
        this.usedAt = usedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getCode() {
        return code;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isUsed() {
        return used;
    }

    public LocalDateTime getUsedAt() {
        return usedAt;
    }
}