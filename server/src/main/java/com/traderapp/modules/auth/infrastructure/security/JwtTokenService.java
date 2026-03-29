package com.traderapp.modules.auth.infrastructure.security;

import org.springframework.stereotype.Service;

import com.traderapp.modules.auth.application.service.JwtService;
import com.traderapp.modules.auth.domain.entities.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;

@Service
public class JwtTokenService implements JwtService {
    private final String secret;
    private final long expirationInMinutes;

    public JwtTokenService(
            @Value("${JWT_SECRET}") String secret,
            @Value("${JWT_EXPIRATION_MINUTES:60}") long expirationInMinutes
    ) {
        this.secret = secret;
        this.expirationInMinutes = expirationInMinutes;
    }

    @Override
    public String generateAccessToken (User user) {

        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(expirationInMinutes * 60);

        return Jwts.builder()
            .subject(user.getId().value().toString())
            .claims(Map.of(
                    "email", user.getEmail().value(),
                    "firstName", user.getFirstName().value()
            ))
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiresAt))
            .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
            .compact();
    }

}
