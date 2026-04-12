package com.traderapp.modules.plan.infrastructure.messaging;

import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.traderapp.modules.auth.application.events.UserEmailVerifiedEvent;
import com.traderapp.modules.plan.application.usecases.CreateTradingPlan;

@Component
public class PlanUserEmailVerifiedEventListener {
    private final CreateTradingPlan createTradingPlan;

    public PlanUserEmailVerifiedEventListener(CreateTradingPlan createTradingPlan){
        this.createTradingPlan = createTradingPlan;
    }

    @RabbitListener(queues = RabbitMqPlanConfig.PLAN_USER_EMAIL_VERIFIED_QUEUE)
    public void handle(UserEmailVerifiedEvent event) {
        createTradingPlan.execute(UUID.fromString(event.userId()));
    }

}
