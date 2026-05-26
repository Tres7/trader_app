package com.traderapp.modules.plan.infrastructure.http.responses;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.traderapp.modules.plan.domain.entities.TradingPlan;
import com.traderapp.modules.plan.domain.enums.SectionKey;

public record TradingPlanResponse(
    UUID id,
    List<SectionResponse> sections,
    List<CustomFieldResponse> customFields,
    LocalDateTime updatedAt
) {
    public record SectionResponse(SectionKey key, String content, String comment) {}
    public record CustomFieldResponse(UUID id, String fieldName, String fieldValue, int displayOrder, String comment) {}

    public static TradingPlanResponse from(TradingPlan plan) {
        return new TradingPlanResponse(
            plan.getId().value(),
            plan.getSections().stream()
                    .map(s -> new SectionResponse(s.getSectionKey(), s.getContent(), s.getComment()))
                    .toList(),
            plan.getCustomFields().stream()
                    .map(f -> new CustomFieldResponse(f.getId(), f.getFieldName(), f.getFieldValue(), f.getDisplayOrder(), f.getComment()))
                    .toList(),
            plan.getUpdatedAt()
        );
    }
}