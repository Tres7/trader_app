package com.traderapp.modules.notification.infrastructure.email;

import com.traderapp.modules.notification.application.ports.output.EmailSender;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class GmailEmailSender implements EmailSender {

    private final JavaMailSender mailSender;
    private final EmailTemplateRenderer emailTemplateRenderer;
    private final String from;

    public GmailEmailSender(
        JavaMailSender mailSender,
        EmailTemplateRenderer emailTemplateRenderer,
        @Value("${MAIL_FROM}") String from
    ) {
        this.mailSender = mailSender;
        this.emailTemplateRenderer = emailTemplateRenderer;
        this.from = from;
    }


    @Override
    public void sendVerificationEmail(String to, String firstName, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Votre code de vérification");
        String body = emailTemplateRenderer.render(
            "templates/emails/verification-email.txt",
            Map.of(
                "firstName", firstName,
                "verificationCode", verificationCode
            )
        );
        message.setText(body);
        mailSender.send(message);
    }

    @Override
    public void sendWelcomeEmail(String to, String firstName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Welcome to Trader App");
        String body = emailTemplateRenderer.render(
            "templates/emails/welcome-email.txt",
            Map.of(
                    "firstName", firstName
            )
        );
        message.setText(body);
        mailSender.send(message);
    }
}
