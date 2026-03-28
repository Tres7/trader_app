package com.traderapp.modules.auth.domain.valueObjects;

import java.time.LocalDate;
import java.util.Objects;

public final class BirthDate {

    private final LocalDate value;

    public BirthDate(LocalDate birthDate) {
        validate(birthDate);
        this.value = birthDate;
    }

    private void validate(LocalDate birthDate) {
        if (birthDate == null) {
            throw new IllegalArgumentException("Birth date cannot be null");
        }

        if (birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Birth date cannot be in the future");
        }
    }

    public LocalDate value() {
        return value;
    }

    public boolean isAdult() {
        return value.plusYears(18).isBefore(LocalDate.now()) || value.plusYears(18).isEqual(LocalDate.now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BirthDate other)) return false;
        return value.equals(other.value);
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
