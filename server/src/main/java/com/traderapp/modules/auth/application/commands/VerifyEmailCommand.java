package com.traderapp.modules.auth.application.commands;

public record VerifyEmailCommand(
    String email,
    String code
) {
    
}
