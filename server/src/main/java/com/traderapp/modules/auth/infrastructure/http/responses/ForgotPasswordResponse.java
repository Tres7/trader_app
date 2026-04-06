package com.traderapp.modules.auth.infrastructure.http.responses;

public record ForgotPasswordResponse(
    String email,
    String message
) {}
