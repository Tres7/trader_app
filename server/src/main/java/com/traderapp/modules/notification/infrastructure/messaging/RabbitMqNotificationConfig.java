package com.traderapp.modules.notification.infrastructure.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.traderapp.modules.auth.infrastructure.messaging.RabbitMqAuthConfig;
import com.traderapp.modules.plan.infrastructure.messaging.RabbitMqPlanConfig;

@Configuration
public class RabbitMqNotificationConfig {

    public static final String VERIFICATION_EMAIL_QUEUE = "notification.verification-email.queue";
    public static final String WELCOME_EMAIL_QUEUE = "notification.welcome-email.queue";
    public static final String PASSWORD_RESET_QUEUE = "notification.password-reset.queue";
    public static final String PASSWORD_RESET_COMPLETED_QUEUE = "notification.password-reset-completed.queue";
    public static final String TRADING_PLAN_CREATED_QUEUE = "notification.trading-plan-created.queue";
    public static final String TRADING_PLAN_UPDATED_QUEUE = "notification.trading-plan-updated.queue";
    public static final String TRADING_PLAN_EXPORTED_QUEUE = "notification.trading-plan-exported.queue";

    @Bean
    public Queue verificationEmailQueue() {
        return new Queue(VERIFICATION_EMAIL_QUEUE);
    }

    @Bean
    public Queue welcomeEmailQueue() {
        return new Queue(WELCOME_EMAIL_QUEUE);
    }

    @Bean
    public Queue passwordResetQueue() {
        return new Queue(PASSWORD_RESET_QUEUE);
    }

    @Bean
    public Queue passwordResetCompletedQueue() {
        return new Queue(PASSWORD_RESET_COMPLETED_QUEUE);
    }

    @Bean
    public Queue tradingPlanCreatedQueue() {
        return new Queue(TRADING_PLAN_CREATED_QUEUE);
    }

    @Bean
    public Queue tradingPlanUpdatedQueue() {
        return new Queue(TRADING_PLAN_UPDATED_QUEUE);
    }

    @Bean
    public Queue tradingPlanExportedQueue() {
        return new Queue(TRADING_PLAN_EXPORTED_QUEUE);
    }

    @Bean
    public Binding verificationEmailBinding(
            Queue verificationEmailQueue,
            @Qualifier("authExchange") DirectExchange authExchange
    ) {
        return BindingBuilder.bind(verificationEmailQueue)
            .to(authExchange)
            .with(RabbitMqAuthConfig.USER_REGISTERED_ROUTING_KEY);
    }

    @Bean
    public Binding welcomeEmailBinding(
        Queue welcomeEmailQueue,
        @Qualifier("authExchange") DirectExchange authExchange
    ) {
        return BindingBuilder.bind(welcomeEmailQueue)
            .to(authExchange)
            .with(RabbitMqAuthConfig.USER_EMAIL_VERIFIED_ROUTING_KEY);
    }

    @Bean
    public Binding passwordResetBinding(
        Queue passwordResetQueue,
        @Qualifier("authExchange") DirectExchange authExchange
    ) {
        return BindingBuilder.bind(passwordResetQueue)
            .to(authExchange)
            .with(RabbitMqAuthConfig.PASSWORD_RESET_ROUTING_KEY);
    }

    @Bean
    public Binding passwordResetCompletedBinding(
        Queue passwordResetCompletedQueue,
        @Qualifier("authExchange") DirectExchange authExchange
    ) {
        return BindingBuilder.bind(passwordResetCompletedQueue)
        .to(authExchange)
        .with(RabbitMqAuthConfig.PASSWORD_RESET_COMPLETED_ROUTING_KEY);
    }

    @Bean
    public Binding tradingPlanCreatedBinding(
        Queue tradingPlanCreatedQueue,
        @Qualifier("planExchange") DirectExchange planExchange
    ) {
        return BindingBuilder.bind(tradingPlanCreatedQueue)
            .to(planExchange)
            .with(RabbitMqPlanConfig.PLAN_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding tradingPlanUpdatedBinding(
        Queue tradingPlanUpdatedQueue,
        @Qualifier("planExchange") DirectExchange planExchange
    ) {
        return BindingBuilder.bind(tradingPlanUpdatedQueue)
            .to(planExchange)
            .with(RabbitMqPlanConfig.PLAN_UPDATED_ROUTING_KEY);
    }

    @Bean
    public Binding tradingPlanExportedBinding(
        Queue tradingPlanExportedQueue,
        @Qualifier("planExchange") DirectExchange planExchange
    ) {
        return BindingBuilder.bind(tradingPlanExportedQueue)
            .to(planExchange)
            .with(RabbitMqPlanConfig.PLAN_EXPORTED_ROUTING_KEY);
    }
}
