package com.traderapp.modules.auth.presentation.rest.requests;

import java.time.LocalDate;

public record UpdateCurrentUserProfileRequest(
    String firstName,
    String lastName,
    LocalDate birthDate,
    String country
) {} 
    

