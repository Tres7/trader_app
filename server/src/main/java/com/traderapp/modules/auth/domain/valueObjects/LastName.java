package com.traderapp.modules.auth.domain.valueObjects;

import java.util.Objects;

public final class LastName {

    private final String value;

    public LastName(String lastName) {
        String normalizedLastName = normalize(lastName);
        validate(normalizedLastName);
        this.value = normalizedLastName;
    }

    private String normalize(String lastName) {
        if (lastName == null) {
            throw new IllegalArgumentException("Last name cannot be null");
        }
        return lastName.trim();
    }

    private void validate(String lastName) {
        if (lastName.isBlank()) {
            throw new IllegalArgumentException("Last name cannot be blank");
        }

        if (lastName.length() > 100) {
            throw new IllegalArgumentException("Last name is too long");
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
        if (!(o instanceof LastName other)) {
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
