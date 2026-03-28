package com.traderapp.modules.auth.domain.valueObjects;

import java.util.Objects;

public final class FirstName {

    private final String value;

    public FirstName(String firstName) {
        String normalizedFirstName = normalize(firstName);
        validate(normalizedFirstName);
        this.value = normalizedFirstName;
    }

    private String normalize(String firstName) {
        if (firstName == null) {
            throw new IllegalArgumentException("First name cannot be null");
        }
        return firstName.trim();
    }

    private void validate(String firstName) {
        if (firstName.isBlank()) {
            throw new IllegalArgumentException("First name cannot be blank");
        }

        if (firstName.length() > 100) {
            throw new IllegalArgumentException("First name is too long");
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
        if (!(o instanceof FirstName other)) {
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
