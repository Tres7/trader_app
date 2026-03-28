package com.traderapp.modules.auth.domain.valueObjects;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public final class Country {

    private static final Set<String> ISO_COUNTRIES = Set.of(Locale.getISOCountries());

    private final String value;

    public Country(String country) {
        String normalizedCountry = normalize(country);
        validate(normalizedCountry);
        this.value = normalizedCountry;
    }

    private String normalize(String country) {
        if (country == null) {
            throw new IllegalArgumentException("Country cannot be null");
        }
        return country.trim().toUpperCase();
    }

    private void validate(String country) {
        if (country.isBlank()) {
            throw new IllegalArgumentException("Country cannot be blank");
        }

        if (!ISO_COUNTRIES.contains(country)) {
            throw new IllegalArgumentException("Country must be a valid ISO country code");
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
        if (!(o instanceof Country other)) {
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
