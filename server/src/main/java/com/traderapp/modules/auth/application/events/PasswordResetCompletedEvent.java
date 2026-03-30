package com.traderapp.modules.auth.application.events;

public record PasswordResetCompletedEvent(
    String userId,
    String email,
    String firstName
) {}
