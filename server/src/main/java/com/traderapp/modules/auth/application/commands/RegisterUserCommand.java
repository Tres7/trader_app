package com.traderapp.modules.auth.application.commands;

import java.time.LocalDate;

public record RegisterUserCommand (
    String firstName,
    String lastName,
    LocalDate birthDate,
    String email,
    String rawPassword,
    String country

 ) {}
