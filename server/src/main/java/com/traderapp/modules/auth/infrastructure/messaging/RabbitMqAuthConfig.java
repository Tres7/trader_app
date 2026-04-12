package com.traderapp.modules.auth.infrastructure.messaging;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;


@Configuration
public class RabbitMqAuthConfig {

    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";
    public static final String USER_REGISTERED_ROUTING_KEY = "notification.email.verification";
    public static final String USER_EMAIL_VERIFIED_ROUTING_KEY = "notification.email.welcome";
    public static final String PASSWORD_RESET_ROUTING_KEY = "notification.password.reset";
    public static final String PASSWORD_RESET_COMPLETED_ROUTING_KEY = "notification.password.reset.completed";


    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(NOTIFICATION_EXCHANGE);
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
