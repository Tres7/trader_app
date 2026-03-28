package com.traderapp.modules.auth.domain.valueObjects;

import java.util.Objects;

public final class PasswordHash {

    private final String value;

    public PasswordHash(String passwordHash) {
        String normalizedPasswordHash = normalize(passwordHash);
        validate(normalizedPasswordHash);
        this.value = normalizedPasswordHash;
    }

    private String normalize(String passwordHash) {
        if (passwordHash == null) {
            throw new IllegalArgumentException("Password hash cannot be null");
        }
        return passwordHash.trim();
    }

    private void validate(String passwordHash) {
        if (passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be blank");
        }

        if (passwordHash.length() < 20) {
            throw new IllegalArgumentException("Password hash format is invalid");
        }
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PasswordHash other)) {
            return false;
        }
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
