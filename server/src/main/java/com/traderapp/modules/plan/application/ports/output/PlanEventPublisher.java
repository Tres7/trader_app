package com.traderapp.modules.plan.application.ports.output;

import com.traderapp.modules.plan.application.events.TradingPlanCreatedEvent;

public interface PlanEventPublisher {
    void publishTradingPlanCreated(TradingPlanCreatedEvent event);
}
