package com.traderapp.modules.auth.infrastructure.http.responses;

public record RegisterUserResponse(
    String userId,
    String email,
    boolean emailVerified,
    String message
) {}
