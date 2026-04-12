package com.traderapp.modules.plan.infrastructure.messaging;

import org.springframework.amqp.core.Binding;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.traderapp.modules.auth.infrastructure.messaging.RabbitMqAuthConfig;

@Configuration
public class RabbitMqPlanConfig {
    public static final String PLAN_USER_EMAIL_VERIFIED_QUEUE = "plan.user-email-verified.queue";
    
    @Bean
    public Queue planUserEmailVerifiedQueue() {
        return new Queue(PLAN_USER_EMAIL_VERIFIED_QUEUE);
    }

    @Bean
    public Binding planUserEmailVerifiedBinding(
        Queue planUserEmailVerifiedQueue,
        DirectExchange notificationExchange
    ) {
        return BindingBuilder
            .bind(planUserEmailVerifiedQueue)
            .to(notificationExchange)
            .with(RabbitMqAuthConfig.USER_EMAIL_VERIFIED_ROUTING_KEY);
    }
}
