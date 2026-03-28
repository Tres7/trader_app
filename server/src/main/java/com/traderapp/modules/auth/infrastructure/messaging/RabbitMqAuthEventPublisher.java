package com.traderapp.modules.auth.infrastructure.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.traderapp.modules.auth.application.events.UserRegisteredEvent;
import com.traderapp.modules.auth.application.ports.output.AuthEventPublisher;

@Component
public class RabbitMqAuthEventPublisher implements AuthEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMqAuthEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishUserRegistered(UserRegisteredEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.NOTIFICATION_EXCHANGE,
                RabbitMqConfig.USER_REGISTERED_ROUTING_KEY,
                event
        );
    }
    
}
