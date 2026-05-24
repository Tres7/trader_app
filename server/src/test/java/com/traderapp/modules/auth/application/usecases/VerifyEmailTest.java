package com.traderapp.modules.auth.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.traderapp.modules.auth.application.commands.VerifyEmailCommand;
import com.traderapp.modules.auth.application.events.UserEmailVerifiedEvent;
import com.traderapp.modules.auth.application.ports.output.AuthEventPublisher;
import com.traderapp.modules.auth.domain.entities.EmailVerificationCode;
import com.traderapp.modules.auth.domain.entities.User;
import com.traderapp.modules.auth.domain.exceptions.EmailAlreadyVerifiedException;
import com.traderapp.modules.auth.domain.exceptions.ExpiredVerificationCodeException;
import com.traderapp.modules.auth.domain.exceptions.InvalidVerificationCodeException;
import com.traderapp.modules.auth.domain.exceptions.UserNotFoundException;
import com.traderapp.modules.auth.domain.repositories.EmailVerificationCodeRepository;
import com.traderapp.modules.auth.domain.repositories.UserRepository;
import com.traderapp.modules.auth.domain.valueObjects.BirthDate;
import com.traderapp.modules.auth.domain.valueObjects.Country;
import com.traderapp.modules.auth.domain.valueObjects.Email;
import com.traderapp.modules.auth.domain.valueObjects.FirstName;
import com.traderapp.modules.auth.domain.valueObjects.LastName;
import com.traderapp.modules.auth.domain.valueObjects.PasswordHash;
import com.traderapp.modules.auth.domain.valueObjects.UserId;

@ExtendWith(MockitoExtension.class)
public class VerifyEmailTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailVerificationCodeRepository emailVerificationCodeRepository;

    @Mock
    private AuthEventPublisher authEventPublisher;

    private VerifyEmail verifyEmail;

    @BeforeEach
    void setUp() {
        verifyEmail = new VerifyEmail(
            userRepository, 
            emailVerificationCodeRepository, 
            authEventPublisher
        );
    }

    @Test
    void should_throw_when_user_is_not_found() {
        VerifyEmailCommand command = new VerifyEmailCommand(
            "john@example.com",
            "123456"
        );

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> verifyEmail.execute(command));

        verify(emailVerificationCodeRepository, never()).findByUserIdAndCode(any(), any());
        verify(authEventPublisher, never()).publishUserEmailVerified(any());
    }
    
    @Test
    void should_throw_when_email_is_already_verified() {
        User user = buildUser(true);
        VerifyEmailCommand command = new VerifyEmailCommand("john@example.com", "123456");

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));

        assertThrows(EmailAlreadyVerifiedException.class, () -> verifyEmail.execute(command));

        verify(emailVerificationCodeRepository, never()).findByUserIdAndCode(any(), any());
        verify(authEventPublisher, never()).publishUserEmailVerified(any());
    }

    @Test
    void should_throw_when_code_is_invalid() {
       User user = buildUser(false);
        
       VerifyEmailCommand command = new VerifyEmailCommand("john@example.com", "999999");

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(emailVerificationCodeRepository.findByUserIdAndCode(user.getId().value(), "999999"))
                .thenReturn(Optional.empty());

        assertThrows(InvalidVerificationCodeException.class, () -> verifyEmail.execute(command));

        verify(authEventPublisher, never()).publishUserEmailVerified(any());
    }

    @Test
    void should_throw_when_code_is_expired() {
        User user = buildUser(false);
        EmailVerificationCode expiredCode = new EmailVerificationCode(
            UUID.randomUUID(),
            user.getId().value(),
            "123456",
            LocalDateTime.now().minusMinutes(1),
            LocalDateTime.now().minusMinutes(20),
            false,
            null
        );

        VerifyEmailCommand command = new VerifyEmailCommand("john@example.com", "123456");

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(emailVerificationCodeRepository.findByUserIdAndCode(user.getId().value(), "123456"))
                .thenReturn(Optional.of(expiredCode));

        assertThrows(ExpiredVerificationCodeException.class, () -> verifyEmail.execute(command));

        verify(authEventPublisher, never()).publishUserEmailVerified(any());
    }

    @Test
    void should_verify_email_and_publish_event() {
        User user = buildUser(false);
        EmailVerificationCode code = EmailVerificationCode.create(
            user.getId().value(),
            "123456",
            LocalDateTime.now().plusMinutes(15)
        );

        VerifyEmailCommand command = new VerifyEmailCommand("john@example.com", "123456");

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(emailVerificationCodeRepository.findByUserIdAndCode(user.getId().value(), "123456"))
                .thenReturn(Optional.of(code));
        when(emailVerificationCodeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        User result = verifyEmail.execute(command);

        assertTrue(result.isEmailVerified());

        verify(emailVerificationCodeRepository).save(code);
        verify(userRepository).save(user);

        ArgumentCaptor<UserEmailVerifiedEvent> eventCaptor = ArgumentCaptor.forClass(UserEmailVerifiedEvent.class);
        verify(authEventPublisher).publishUserEmailVerified(eventCaptor.capture());

        UserEmailVerifiedEvent event = eventCaptor.getValue();
        assertEquals(user.getId().value().toString(), event.userId());
        assertEquals(user.getEmail().value(), event.email());
        assertEquals(user.getFirstName().value(), event.firstName());
    }

    private User buildUser(boolean emailVerified) {
        return new User(
            UserId.generate(),
            new FirstName("John"),
            new LastName("Doe"),
            new BirthDate(LocalDate.of(1998, 5, 10)),
            new Email("john@example.com"),
            new PasswordHash("hashed-password-value-12345"),
            new Country("FR"),
            emailVerified,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

}
