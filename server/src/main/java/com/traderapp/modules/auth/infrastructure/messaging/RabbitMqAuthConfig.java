package com.traderapp.modules.auth.infrastructure.messaging;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqAuthConfig {

    public static final String AUTH_EXCHANGE = "auth.exchange";
    public static final String USER_REGISTERED_ROUTING_KEY = "auth.user.registered";
    public static final String USER_EMAIL_VERIFIED_ROUTING_KEY = "auth.email.verified";
    public static final String PASSWORD_RESET_ROUTING_KEY = "auth.password.reset.requested";
    public static final String PASSWORD_RESET_COMPLETED_ROUTING_KEY = "auth.password.reset.completed";

    @Bean("authExchange")
    public DirectExchange authExchange() {
        return new DirectExchange(AUTH_EXCHANGE);
    }
}
