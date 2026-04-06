package com.traderapp.modules.auth.presentation.rest.requests;

public record LoginRequest(
    String email,
    String password
) {}
