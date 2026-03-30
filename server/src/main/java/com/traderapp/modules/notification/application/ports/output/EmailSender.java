package com.traderapp.modules.notification.application.ports.output;

public interface EmailSender {

    void sendVerificationEmail(String to, String firstName, String verificationCode);

    void sendWelcomeEmail(String to, String firstName);

    void sendPasswordResetEmail(String to, String firstName, String resetCode);
    
}