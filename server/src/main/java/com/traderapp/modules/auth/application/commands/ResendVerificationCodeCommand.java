package com.traderapp.modules.auth.application.commands;

public record ResendVerificationCodeCommand(
    String email
) {}
