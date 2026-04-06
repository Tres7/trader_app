package com.traderapp.modules.notification.application.usecases;

import com.traderapp.modules.notification.application.ports.output.EmailSender;
import org.springframework.stereotype.Service;

@Service
public class SendVerificationEmail {

    private final EmailSender emailSender;

    public SendVerificationEmail(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void execute(String to, String firstName, String verificationCode) {
        emailSender.sendVerificationEmail(to, firstName, verificationCode);
    }
}