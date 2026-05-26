package com.traderapp.modules.plan.domain.entities;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import com.traderapp.modules.plan.domain.valueObjects.TradingPlanId;

public class TradingPlanCustomField {
    private final UUID id;
    private final TradingPlanId tradingPlanId;
    private String fieldName;
    private String fieldValue;
    private int displayOrder;
    private String comment;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TradingPlanCustomField(
        UUID id,
        TradingPlanId tradingPlanId,
        String fieldName,
        String fieldValue,
        int displayOrder,
        String comment,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
            this.id = Objects.requireNonNull(id, "CustomField id cannot be null");
            this.tradingPlanId = Objects.requireNonNull(tradingPlanId, "TradingPlan id cannot be null");
            this.fieldName = Objects.requireNonNull(fieldName, "Field name cannot be null");
            this.fieldValue = fieldValue;
            this.displayOrder = displayOrder;
            this.comment = comment;
            this.createdAt = Objects.requireNonNull(createdAt, "createdAt cannot be null");
            this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt cannot be null");
    }

    public void update(String fieldName, String fieldValue, int displayOrder) {
        this.fieldName = Objects.requireNonNull(fieldName, "Field name cannot be null");
        this.fieldValue = fieldValue;
        this.displayOrder = displayOrder;
        touch();
    }

    public void updateComment(String comment) {
        this.comment = comment;
        touch();
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() { 
        return id; 
    }

    public TradingPlanId getTradingPlanId() { 
        return tradingPlanId; 
    }

    public String getFieldName() { 
        return fieldName; 
    }

    public String getFieldValue() { 
        return fieldValue; 
    }

    public int getDisplayOrder() { 
        return displayOrder; 
    }

    public String getComment() { 
        return comment; 
    }

    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }

    public LocalDateTime getUpdatedAt() { 
        return updatedAt; 
    }
}
