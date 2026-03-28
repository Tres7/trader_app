package com.traderapp.modules.notification.infrastructure.messaging;

import com.traderapp.modules.auth.application.events.UserRegisteredEvent;
import com.traderapp.modules.auth.infrastructure.messaging.RabbitMqConfig;
import com.traderapp.modules.notification.application.usecases.SendVerificationEmail;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class UserRegisteredEventListener {

    private final SendVerificationEmail sendVerificationEmail;

    public UserRegisteredEventListener(SendVerificationEmail sendVerificationEmail) {
        this.sendVerificationEmail = sendVerificationEmail;
    }

    @RabbitListener(queues = RabbitMqConfig.VERIFICATION_EMAIL_QUEUE)
    public void handle(UserRegisteredEvent event) {
        sendVerificationEmail.execute(
                event.email(),
                event.firstName(),
                event.verificationCode()
        );
    }
}