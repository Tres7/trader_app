package com.traderapp.modules.auth.presentation.rest.requests;

public record ResetPasswordRequest(
    String email,
    String code,
    String newPassword
) {}
