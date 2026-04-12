package com.traderapp.modules.notification.infrastructure.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.traderapp.modules.auth.infrastructure.messaging.RabbitMqAuthConfig;

@Configuration
public class RabbitMqNotificationConfig {

    public static final String VERIFICATION_EMAIL_QUEUE = "notification.verification-email.queue";
    public static final String WELCOME_EMAIL_QUEUE = "notification.welcome-email.queue";
    public static final String PASSWORD_RESET_QUEUE = "notification.password-reset.queue";
    public static final String PASSWORD_RESET_COMPLETED_QUEUE = "notification.password-reset-completed.queue";

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
    public Binding verificationEmailBinding(DirectExchange notificationExchange) {
        return BindingBuilder.bind(verificationEmailQueue())
                .to(notificationExchange)
                .with(RabbitMqAuthConfig.USER_REGISTERED_ROUTING_KEY);
    }

    @Bean
    public Binding welcomeEmailBinding(DirectExchange notificationExchange) {
        return BindingBuilder.bind(welcomeEmailQueue())
            .to(notificationExchange)
            .with(RabbitMqAuthConfig.USER_EMAIL_VERIFIED_ROUTING_KEY);
    }

    @Bean
    public Binding passwordResetBinding(DirectExchange notificationExchange) {
        return BindingBuilder.bind(passwordResetQueue())
            .to(notificationExchange)
            .with(RabbitMqAuthConfig.PASSWORD_RESET_ROUTING_KEY);
    }

    @Bean
    public Binding passwordResetCompletedBinding(DirectExchange notificationExchange) {
        return BindingBuilder.bind(passwordResetCompletedQueue())
            .to(notificationExchange)
            .with(RabbitMqAuthConfig.PASSWORD_RESET_COMPLETED_ROUTING_KEY);
    }
}