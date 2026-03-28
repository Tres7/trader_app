package com.traderapp.modules.auth.presentation.rest.requests;

import java.time.LocalDate;

public record RegisterUserRequest(
        String firstName,
        String lastName,
        LocalDate birthDate,
        String email,
        String password,
        String country
) {
}