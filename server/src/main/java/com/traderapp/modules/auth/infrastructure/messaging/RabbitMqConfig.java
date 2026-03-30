package com.traderapp.modules.auth.infrastructure.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;


@Configuration
public class RabbitMqConfig {

    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";
    public static final String VERIFICATION_EMAIL_QUEUE = "notification.verification-email.queue";
    public static final String USER_REGISTERED_ROUTING_KEY = "notification.email.verification";
    public static final String WELCOME_EMAIL_QUEUE = "notification.welcome-email.queue";
    public static final String USER_EMAIL_VERIFIED_ROUTING_KEY = "notification.email.welcome";
    public static final String PASSWORD_RESET_QUEUE = "notification.password-reset.queue";
    public static final String PASSWORD_RESET_ROUTING_KEY = "notification.password.reset";
    public static final String PASSWORD_RESET_COMPLETED_QUEUE = "notification.password-reset-completed.queue";
    public static final String PASSWORD_RESET_COMPLETED_ROUTING_KEY = "notification.password.reset.completed";




    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(NOTIFICATION_EXCHANGE);
    }

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
    public Binding verificationEmailBinding(
            Queue verificationEmailQueue,
            DirectExchange notificationExchange
    ) {
        return BindingBuilder
                .bind(verificationEmailQueue)
                .to(notificationExchange)
                .with(USER_REGISTERED_ROUTING_KEY);
    }

    @Bean
    public Binding welcomeEmailBinding(
            Queue welcomeEmailQueue,
            DirectExchange notificationExchange
    ) {
        return BindingBuilder
                .bind(welcomeEmailQueue)
                .to(notificationExchange)
                .with(USER_EMAIL_VERIFIED_ROUTING_KEY);
    }

    @Bean
    public Binding passwordResetBinding(
            Queue passwordResetQueue,
            DirectExchange notificationExchange
    ) {
        return BindingBuilder
                .bind(passwordResetQueue)
                .to(notificationExchange)
                .with(PASSWORD_RESET_ROUTING_KEY);
    }

    @Bean
    public Binding passwordResetCompletedBinding(
            Queue passwordResetCompletedQueue,
            DirectExchange notificationExchange
    ) {
        return BindingBuilder
                .bind(passwordResetCompletedQueue)
                .to(notificationExchange)
                .with(PASSWORD_RESET_COMPLETED_ROUTING_KEY);
    }


    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

}
