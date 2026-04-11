package com.traderapp.modules.plan.domain.valueObjects;

import java.util.Objects;
import java.util.UUID;


public final class TradingPlanId {
    private final UUID value;

    public TradingPlanId (UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("TradingPlan id cannot be null");
        }
        this.value = value;
    }

    public static TradingPlanId generate() {
        return new TradingPlanId(UUID.randomUUID());
    }

    public static TradingPlanId from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("TradingPlan id cannot be null or blank");
        }
        return new TradingPlanId(UUID.fromString(value));
    }

    public UUID value() {
        return value;
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
