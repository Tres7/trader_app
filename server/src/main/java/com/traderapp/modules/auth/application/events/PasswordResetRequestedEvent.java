package com.traderapp.modules.auth.application.events;

public record PasswordResetRequestedEvent(
    String userId,
    String email,
    String firstName,
    String resetCode
) {}
