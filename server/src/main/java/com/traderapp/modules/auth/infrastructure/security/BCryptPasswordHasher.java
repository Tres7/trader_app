package com.traderapp.modules.auth.infrastructure.security;

import com.traderapp.modules.auth.application.service.PasswordHasher;
import com.traderapp.modules.auth.domain.valueObjects.PasswordHash;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordHasher implements PasswordHasher {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public PasswordHash hash(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Raw password cannot be null or blank");
        }

        String hashedPassword = passwordEncoder.encode(rawPassword);
        return new PasswordHash(hashedPassword);
    }
}