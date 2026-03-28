package com.traderapp.modules.auth.application.events;

public record UserRegisteredEvent(
    String userId,
    String email,
    String firstName,
    String verificationCode
) {
} 
