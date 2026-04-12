package com.traderapp.modules.plan.infrastructure.persistence.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="trading_plan")
public class TradingPlanEntity {
    @Id
    private UUID id;

    @Column(name="user_id", nullable = false, unique = true)
    private UUID userId;

    @OneToMany(mappedBy = "tradingPlan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<TradingPlanSectionEntity> sections = new ArrayList<>();

    @OneToMany(mappedBy = "tradingPlan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<TradingPlanCustomFieldEntity> customFields = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected TradingPlanEntity() {}

        public TradingPlanEntity(UUID id, UUID userId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { 
        return id; 
    }

    public UUID getUserId() { 
        return userId; 
    }

    public List<TradingPlanSectionEntity> getSections() { 
        return sections; 
    }

    public List<TradingPlanCustomFieldEntity> getCustomFields() { 
        return customFields; 
    }

    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }

    public LocalDateTime getUpdatedAt() { 
        return updatedAt; 
    }

    public void setUpdatedAt(LocalDateTime updatedAt) { 
        this.updatedAt = updatedAt; 
    }
}
