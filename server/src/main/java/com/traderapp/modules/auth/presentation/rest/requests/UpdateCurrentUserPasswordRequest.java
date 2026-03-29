package com.traderapp.modules.auth.presentation.rest.requests;

public record UpdateCurrentUserPasswordRequest(
        String currentPassword,
        String newPassword
) {
}