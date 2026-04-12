package com.traderapp.modules.notification.infrastructure.messaging;

import com.traderapp.modules.auth.application.events.PasswordResetRequestedEvent;
import com.traderapp.modules.notification.application.ports.output.EmailSender;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PasswordResetRequestedEventListener {

    private final EmailSender emailSender;

    public PasswordResetRequestedEventListener(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    @RabbitListener(queues = RabbitMqNotificationConfig.PASSWORD_RESET_QUEUE)
    public void handle(PasswordResetRequestedEvent event) {
        emailSender.sendPasswordResetEmail(
                event.email(),
                event.firstName(),
                event.resetCode()
        );
    }
}