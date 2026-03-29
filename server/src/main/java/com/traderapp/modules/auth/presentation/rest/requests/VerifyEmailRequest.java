package com.traderapp.modules.auth.presentation.rest.requests;

public record VerifyEmailRequest(
    String email,
    String code
) {
    
}
