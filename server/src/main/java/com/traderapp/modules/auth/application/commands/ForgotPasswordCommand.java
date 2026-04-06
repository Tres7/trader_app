package com.traderapp.modules.auth.application.commands;

public record ForgotPasswordCommand(
        String email
) {
}