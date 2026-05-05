package com.traderapp.modules.plan.presentation.rest.requests;

import java.util.List;

import com.traderapp.modules.plan.domain.enums.SectionKey;

public record UpdateTradingPlanRequest(
        List<SectionRequest> sections,
        List<CustomFieldRequest> customFields
) {
    public record SectionRequest(SectionKey key, String content, String comment) {}
    public record CustomFieldRequest(String fieldName, String fieldValue, int displayOrder) {}
}