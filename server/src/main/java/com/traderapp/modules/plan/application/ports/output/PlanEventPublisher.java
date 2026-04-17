package com.traderapp.modules.plan.application.ports.output;

import com.traderapp.modules.plan.application.events.TradingPlanCreatedEvent;
import com.traderapp.modules.plan.application.events.TradingPlanUpdatedEvent;

public interface PlanEventPublisher {
    void publishTradingPlanCreated(TradingPlanCreatedEvent event);

    void publishTradingPlanUpdated(TradingPlanUpdatedEvent event);
}
