package com.traderapp.modules.auth.infrastructure.http.responses;

public record VerifyEmailResponse(
    String email,
    boolean emailVerified,
    String message
) {
    
}
