package com.traderapp.modules.auth.application.usecases;

import com.traderapp.modules.auth.application.commands.LoginCommand;
import com.traderapp.modules.auth.application.dto.LoginResult;
import com.traderapp.modules.auth.application.service.JwtService;
import com.traderapp.modules.auth.application.service.PasswordVerifier;
import com.traderapp.modules.auth.domain.entities.User;
import com.traderapp.modules.auth.domain.exceptions.EmailNotVerifiedException;
import com.traderapp.modules.auth.domain.exceptions.InvalidCredentialsException;
import com.traderapp.modules.auth.domain.repositories.UserRepository;
import com.traderapp.modules.auth.domain.valueObjects.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginTest {

    @Mock UserRepository userRepository;
    @Mock PasswordVerifier passwordVerifier;
    @Mock JwtService jwtService;

    @InjectMocks Login login;

    private User verifiedUser() {
        User user = new User(
            new UserId(UUID.randomUUID()),
            new FirstName("John"),
            new LastName("Doe"),
            new BirthDate(LocalDate.of(1990, 1, 1)),
            new Email("john@example.com"),
            new PasswordHash("$2a$10$stubbedHashForUnitTests12"),
            new Country("FR"),
            true,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        return user;
    }

    private User unverifiedUser() {
        return new User(
            new UserId(UUID.randomUUID()),
            new FirstName("Jane"),
            new LastName("Doe"),
            new BirthDate(LocalDate.of(1995, 5, 5)),
            new Email("jane@example.com"),
            new PasswordHash("$2a$10$stubbedHashForUnitTests12"),
            new Country("FR"),
            false,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

    @Test
    void execute_withValidCredentials_returnsLoginResult() {
        // Arrange
        User user = verifiedUser();
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(passwordVerifier.matches(any(), any())).thenReturn(true);
        when(jwtService.generateAccessToken(user)).thenReturn("token-abc");

        // Act
        LoginResult result = login.execute(new LoginCommand("john@example.com", "Secret123!"));

        // Assert
        assertThat(result.accessToken()).isEqualTo("token-abc");
        assertThat(result.email()).isEqualTo("john@example.com");
        assertThat(result.firstName()).isEqualTo("John");
    }

    @Test
    void execute_withUnknownEmail_throwsInvalidCredentialsException() {
        // Arrange
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> login.execute(new LoginCommand("unknown@example.com", "pass")))
            .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void execute_withWrongPassword_throwsInvalidCredentialsException() {
        // Arrange
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(verifiedUser()));
        when(passwordVerifier.matches(any(), any())).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> login.execute(new LoginCommand("john@example.com", "wrong")))
            .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void execute_withUnverifiedEmail_throwsEmailNotVerifiedException() {
        // Arrange
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(unverifiedUser()));
        when(passwordVerifier.matches(any(), any())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> login.execute(new LoginCommand("jane@example.com", "Secret123!")))
            .isInstanceOf(EmailNotVerifiedException.class);
    }
}
