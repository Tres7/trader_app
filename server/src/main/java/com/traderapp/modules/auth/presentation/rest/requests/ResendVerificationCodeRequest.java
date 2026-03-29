package com.traderapp.modules.auth.presentation.rest.requests;

public record ResendVerificationCodeRequest(
    String email
) {}
