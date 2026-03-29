package com.traderapp.modules.auth.application.dto;

public record LoginResult(
    String accessToken,
    String userId,
    String email,
    String firstName
) {}
