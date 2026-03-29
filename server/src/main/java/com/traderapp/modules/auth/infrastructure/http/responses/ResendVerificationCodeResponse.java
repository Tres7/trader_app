package com.traderapp.modules.auth.infrastructure.http.responses;

public record ResendVerificationCodeResponse(
    String email,
    String message
) {}
