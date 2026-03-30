package com.traderapp.modules.notification.infrastructure.messaging;

import com.traderapp.modules.auth.application.events.PasswordResetCompletedEvent;
import com.traderapp.modules.auth.infrastructure.messaging.RabbitMqConfig;
import com.traderapp.modules.notification.application.ports.output.EmailSender;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PasswordResetCompletedEventListener {

    private final EmailSender emailSender;

    public PasswordResetCompletedEventListener(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    @RabbitListener(queues = RabbitMqConfig.PASSWORD_RESET_COMPLETED_QUEUE)
    public void handle(PasswordResetCompletedEvent event) {
        emailSender.sendPasswordResetConfirmationEmail(
                event.email(),
                event.firstName()
        );
    }
}