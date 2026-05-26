package com.traderapp.modules.plan.application.commands;

import java.util.List;

import com.traderapp.modules.plan.domain.enums.SectionKey;

public record UpdateTradingPlanCommand(
    List<SectionCommand> sections,
    List<CustomFieldCommand> customFields
) {
    public record SectionCommand(SectionKey key, String content, String comment) {}
    public record CustomFieldCommand(String fieldName, String fieldValue, int displayOrder, String comment) {}
}
