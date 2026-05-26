package com.traderapp.modules.plan.application.events;

public record TradingPlanExportedEvent(
    String tradingPlanId,
    String userId
) {}
