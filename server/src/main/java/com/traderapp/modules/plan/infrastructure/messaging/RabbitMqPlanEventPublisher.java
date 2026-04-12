package com.traderapp.modules.plan.infrastructure.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.traderapp.modules.auth.infrastructure.messaging.RabbitMqAuthConfig;
import com.traderapp.modules.plan.application.events.TradingPlanCreatedEvent;
import com.traderapp.modules.plan.application.ports.output.PlanEventPublisher;

@Component
public class RabbitMqPlanEventPublisher implements PlanEventPublisher{
    private final RabbitTemplate rabbitTemplate;

    public RabbitMqPlanEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishTradingPlanCreated(TradingPlanCreatedEvent event) {
        rabbitTemplate.convertAndSend(
            RabbitMqAuthConfig.NOTIFICATION_EXCHANGE,
            RabbitMqPlanConfig.PLAN_CREATED_ROUTING_KEY,
            event
        );
    }
}
