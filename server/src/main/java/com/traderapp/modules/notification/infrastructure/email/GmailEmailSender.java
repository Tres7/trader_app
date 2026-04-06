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
        message.setSubject("Bienvenue sur Trader App");
        String body = emailTemplateRenderer.render(
            "templates/emails/welcome-email.txt",
            Map.of(
                    "firstName", firstName
            )
        );
        message.setText(body);
        mailSender.send(message);
    }

    @Override
    public void sendPasswordResetEmail(String to, String firstName, String resetCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Votre code de reinitialisation");

        String body = emailTemplateRenderer.render(
                "templates/emails/password-reset-email.txt",
                Map.of(
                        "firstName", firstName,
                        "resetCode", resetCode
                )
        );

        message.setText(body);
        mailSender.send(message);
    }

    @Override
    public void sendPasswordResetConfirmationEmail(String to, String firstName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Votre mot de passe a ete reinitialise");

        String body = emailTemplateRenderer.render(
                "templates/emails/password-reset-confirmation-email.txt",
                Map.of("firstName", firstName)
        );

        message.setText(body);
        mailSender.send(message);
    }

}
