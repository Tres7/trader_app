package com.traderapp.modules.auth.infrastructure.http.responses;

public record ResetPasswordResponse(
    String email,
    String message
) {}
