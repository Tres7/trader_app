package com.traderapp.modules.plan.application.events;

public record TradingPlanCreatedEvent(
    String tradingPlanId,
    String userId
) {}
