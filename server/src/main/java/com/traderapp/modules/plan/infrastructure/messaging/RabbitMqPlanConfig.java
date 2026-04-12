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
    public static final String PLAN_CREATED_ROUTING_KEY = "plan.trading-plan.created";
    public static final String PLAN_CREATED_NOTIFICATION_QUEUE = "notification.trading-plan-created.queue"; 
    
    @Bean
    public Queue planUserEmailVerifiedQueue() {
        return new Queue(PLAN_USER_EMAIL_VERIFIED_QUEUE);
    }

    @Bean
    public Queue planCreatedNotificationQueue() {
        return new Queue(PLAN_CREATED_NOTIFICATION_QUEUE);
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

    @Bean
    public Binding planCreatedNotificationBinding(
        Queue planCreatedNotificationQueue,
        DirectExchange notificationExchange
    ) {
        return BindingBuilder
            .bind(planCreatedNotificationQueue)
            .to(notificationExchange)
            .with(PLAN_CREATED_ROUTING_KEY);
    }
}
