package com.traderapp.modules.auth.domain.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;


public class EmailVerificationCodeTest {
    
    @Test
    void should_create_a_usable_verification_code() {
        EmailVerificationCode emailVerificationCode = EmailVerificationCode.create(
            UUID.randomUUID(),
            "123456",
            LocalDateTime.now().plusMinutes(15)
        );

        assertTrue(emailVerificationCode.canBeUsed());
        assertFalse(emailVerificationCode.isUsed());
        assertFalse(emailVerificationCode.isExpired());
    }

    @Test
    void should_mark_code_as_used() {
        EmailVerificationCode emailVerificationCode = EmailVerificationCode.create(
            UUID.randomUUID(),
            "123456",
            LocalDateTime.now().plusMinutes(15)
        );

        emailVerificationCode.markAsUsed();
        assertTrue(emailVerificationCode.isUsed());
        assertNotNull(emailVerificationCode.getUsedAt());
        assertFalse(emailVerificationCode.canBeUsed());
    }

    @Test
    void should_throw_when_marking_already_used_code() {
        EmailVerificationCode emailVerificationCode = new EmailVerificationCode(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "123456",
            LocalDateTime.now().plusMinutes(15),
            LocalDateTime.now(),
            true,
            LocalDateTime.now()
        );

        IllegalStateException exception = assertThrows(
            IllegalStateException.class, 
            emailVerificationCode::markAsUsed
        );

        assertEquals("Verification code has already been used", exception.getMessage());
    }

    @Test
    void should_detect_expired_code() {
        EmailVerificationCode emailVerificationCode = new EmailVerificationCode(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "123456",
            LocalDateTime.now().minusMinutes(1),
            LocalDateTime.now().minusMinutes(20),
            false,
            null
        );
        assertTrue(emailVerificationCode.isExpired());
        assertFalse(emailVerificationCode.canBeUsed());
    }

    @Test
    void should_throw_when_marking_expired_code_as_used() {
        EmailVerificationCode code = new EmailVerificationCode(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "123456",
            LocalDateTime.now().minusMinutes(1),
            LocalDateTime.now().minusMinutes(20),
            false,
            null
        );

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                code::markAsUsed
        );

        assertEquals("Verification code has expired", exception.getMessage());
    }
}
