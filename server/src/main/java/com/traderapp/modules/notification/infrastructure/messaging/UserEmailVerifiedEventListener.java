package com.traderapp.modules.notification.infrastructure.messaging;

import com.traderapp.modules.auth.application.events.UserEmailVerifiedEvent;
import com.traderapp.modules.auth.infrastructure.messaging.RabbitMqConfig;
import com.traderapp.modules.notification.application.ports.output.EmailSender;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class UserEmailVerifiedEventListener {

    private final EmailSender emailSender;

    public UserEmailVerifiedEventListener(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    @RabbitListener(queues = RabbitMqConfig.WELCOME_EMAIL_QUEUE)
    public void handle(UserEmailVerifiedEvent event) {
        emailSender.sendWelcomeEmail(
                event.email(),
                event.firstName()
        );
    }
}
