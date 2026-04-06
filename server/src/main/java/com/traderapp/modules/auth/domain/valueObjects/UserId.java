package com.traderapp.modules.auth.domain.valueObjects;

import java.util.Objects;
import java.util.UUID;

public final class UserId {
    private final UUID value;

    public UserId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("User id cannot be empty");
        }
        this.value = value;
    }

    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }

        public static UserId from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("User id cannot be null or blank");
        }
        return new UserId(UUID.fromString(value));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserId userId)) return false;
        return value.equals(userId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}