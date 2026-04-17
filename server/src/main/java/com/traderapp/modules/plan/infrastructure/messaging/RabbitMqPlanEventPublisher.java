package com.traderapp.modules.plan.infrastructure.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.traderapp.modules.plan.application.events.TradingPlanCreatedEvent;
import com.traderapp.modules.plan.application.events.TradingPlanUpdatedEvent;
import com.traderapp.modules.plan.application.ports.output.PlanEventPublisher;

@Component
public class RabbitMqPlanEventPublisher implements PlanEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMqPlanEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishTradingPlanCreated(TradingPlanCreatedEvent event) {
        rabbitTemplate.convertAndSend(
            RabbitMqPlanConfig.PLAN_EXCHANGE,
            RabbitMqPlanConfig.PLAN_CREATED_ROUTING_KEY,
            event
        );
    }

    @Override
    public void publishTradingPlanUpdated(TradingPlanUpdatedEvent event) {
        rabbitTemplate.convertAndSend(
            RabbitMqPlanConfig.PLAN_EXCHANGE,
            RabbitMqPlanConfig.PLAN_UPDATED_ROUTING_KEY,
            event
        );
    }
}
