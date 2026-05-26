package com.traderapp.modules.plan.domain.entities;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Objects;

import com.traderapp.modules.plan.domain.enums.SectionKey;
import com.traderapp.modules.plan.domain.valueObjects.TradingPlanId;

public class TradingPlanSection {
    private final UUID id;
    private final TradingPlanId tradingPlanId;
    private final SectionKey sectionKey;
    private String content;
    private String comment;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TradingPlanSection(
        UUID id,
        TradingPlanId tradingPlanId,
        SectionKey sectionKey,
        String content,
        String comment,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "Section id cannot be null");
        this.tradingPlanId = Objects.requireNonNull(tradingPlanId, "TradingPlan id cannot be null");
        this.sectionKey = Objects.requireNonNull(sectionKey, "Section key cannot be null");
        this.content = content;
        this.comment = comment;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt cannot be null");
    }

    public void updateContent(String content) {
        this.content = content;
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

    public SectionKey getSectionKey() { 
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
