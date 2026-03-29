package com.traderapp.modules.auth.infrastructure.http.responses;

public record GetCurrentUserResponse(
    String userId,
    String firstName,
    String lastName,
    String email,
    String country,
    String birthDate,
    boolean emailVerified
) {}
