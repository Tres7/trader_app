package com.traderapp.modules.auth.presentation.rest.requests;

public record LoginResponse(
    String accessToken,
    String tokenType,
    String userId,
    String email,
    String firstName
) {}
