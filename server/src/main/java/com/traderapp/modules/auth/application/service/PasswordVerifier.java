package com.traderapp.modules.auth.application.service;

import com.traderapp.modules.auth.domain.valueObjects.PasswordHash;

public interface PasswordVerifier {
    boolean matches(String rawPassword, PasswordHash passwordHash);
}
