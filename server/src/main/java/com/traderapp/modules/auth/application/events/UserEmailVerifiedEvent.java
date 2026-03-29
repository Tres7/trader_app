package com.traderapp.modules.auth.application.events;

public record UserEmailVerifiedEvent(
        String userId,
        String email,
        String firstName
) {
}