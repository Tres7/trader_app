package com.traderapp.modules.plan.application.events;

public record TradingPlanUpdatedEvent(
    String tradingPlanId,
    String userId
) {}
