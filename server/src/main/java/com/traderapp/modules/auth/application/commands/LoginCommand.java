package com.traderapp.modules.auth.application.commands;

public record LoginCommand(
    String email,
    String password
) {}
