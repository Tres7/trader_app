package com.traderapp.modules.auth.infrastructure.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.traderapp.modules.auth.application.service.PasswordVerifier;
import com.traderapp.modules.auth.domain.valueObjects.PasswordHash;

@Component
public class BCryptPasswordVerifier implements PasswordVerifier {

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Override
    public boolean matches(String rawPassword, PasswordHash passwordHash) {
        if (rawPassword == null || rawPassword.isBlank()) {
            return false;
        }
        return bCryptPasswordEncoder.matches(rawPassword, passwordHash.value());
    }
    
}
