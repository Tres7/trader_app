package com.traderapp.modules.auth.domain.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import java.util.Objects;

public class PasswordResetCode {
    private final UUID id;
    private final UUID userId;
    private final String code;
    private final LocalDateTime expiresAt;
    private final LocalDateTime createdAt;
    private boolean used;
    private LocalDateTime usedAt;
   

    public PasswordResetCode(
            UUID id,
            UUID userId,
            String code,
            LocalDateTime expiresAt,
            LocalDateTime createdAt,
            boolean used,
            LocalDateTime usedAt
    ) {
        this.id = Objects.requireNonNull(id);
        this.userId = Objects.requireNonNull(userId);
        this.code = validateCode(code);
        this.expiresAt = Objects.requireNonNull(expiresAt);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.used = used;
        this.usedAt = usedAt;
    }
    
    public static PasswordResetCode create(UUID userId, String code, LocalDateTime expiresAt) {
        LocalDateTime now = LocalDateTime.now();

        return new PasswordResetCode(
                UUID.randomUUID(),
                userId,
                code,
                expiresAt,
                now,
                false,
                null
        );
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean canBeUsed() {
        return !used && !isExpired();
    }

    public void markAsUsed() {
        if (used) {
            throw new IllegalStateException("Password reset code has already been used");
        }

        if (isExpired()) {
            throw new IllegalStateException("Password reset code has expired");
        }

        this.used = true;
        this.usedAt = LocalDateTime.now();
    }

    private String validateCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Password reset code cannot be blank");
        }

        String normalizedCode = code.trim();

        if (!normalizedCode.matches("\\d{6}")) {
            throw new IllegalArgumentException("Password reset code must contain exactly 6 digits");
        }

        return normalizedCode;
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
