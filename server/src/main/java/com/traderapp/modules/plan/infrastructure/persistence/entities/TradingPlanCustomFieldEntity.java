package com.traderapp.modules.plan.infrastructure.persistence.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "trading_plan_custom_field")
public class TradingPlanCustomFieldEntity {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trading_plan_id", nullable = false)
    private TradingPlanEntity tradingPlan;

    @Column(name="field_name", nullable = false)
    private String fieldName;

    @Column(name = "field_value", columnDefinition = "TEXT")
    private String fieldValue;


    @Column(name = "display_order", nullable = false)
    private int displayOrder;
    
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected TradingPlanCustomFieldEntity() {}

    public TradingPlanCustomFieldEntity(
        UUID id,
        TradingPlanEntity tradingPlan,
        String fieldName,
        String fieldValue,
        int displayOrder,
        String comment,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
        this.id = id;
        this.tradingPlan = tradingPlan;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.displayOrder = displayOrder;
        this.comment = comment;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { 
        return id; 
    }

    public TradingPlanEntity getTradingPlan() { 
        return tradingPlan; 
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
