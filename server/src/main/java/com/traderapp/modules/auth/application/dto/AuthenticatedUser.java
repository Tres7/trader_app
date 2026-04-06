package com.traderapp.modules.auth.application.dto;

public record AuthenticatedUser(
    String userId,
    String email
) {}
