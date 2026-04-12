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
@Table(name="trading_plan_section")
public class TradingPlanSectionEntity {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trading_plan_id", nullable = false)
    private TradingPlanEntity tradingPlan;

    @Column(name = "section_key", nullable = false)
    private String sectionKey;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected TradingPlanSectionEntity() {}
    
    public TradingPlanSectionEntity(
        UUID id,
        TradingPlanEntity tradingPlan,
        String sectionKey,
        String content,
        String comment,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
            this.id = id;
            this.tradingPlan = tradingPlan;
            this.sectionKey = sectionKey;
            this.content = content;
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

    public String getSectionKey() { 
        return sectionKey; 
    }

    public String getContent() { 
        return content; 
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
