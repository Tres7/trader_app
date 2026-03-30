package com.traderapp.modules.auth.application.commands;

public record ResetPasswordCommand(
    String email,
    String code,
    String newPassword
) {}
