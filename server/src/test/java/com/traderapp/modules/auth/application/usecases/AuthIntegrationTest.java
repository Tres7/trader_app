package com.traderapp.modules.auth.application.usecases;

import com.traderapp.BaseIntegrationTest;
import com.traderapp.modules.auth.application.commands.LoginCommand;
import com.traderapp.modules.auth.application.commands.RegisterUserCommand;
import com.traderapp.modules.auth.application.commands.VerifyEmailCommand;
import com.traderapp.modules.auth.application.dto.LoginResult;
import com.traderapp.modules.auth.domain.entities.User;
import com.traderapp.modules.auth.domain.repositories.EmailVerificationCodeRepository;
import com.traderapp.modules.auth.domain.repositories.UserRepository;
import com.traderapp.modules.auth.domain.valueObjects.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AuthIntegrationTest extends BaseIntegrationTest {

    @Autowired 
    RegisterUser registerUser;

    @Autowired 
    VerifyEmail verifyEmail;

    @Autowired 
    Login login;

    @Autowired 
    UserRepository userRepository;

    @Autowired 
    EmailVerificationCodeRepository emailVerificationCodeRepository;

    @Autowired 
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanup() {
        jdbcTemplate.execute("TRUNCATE TABLE email_verification_codes, users CASCADE");
    }

    private RegisterUserCommand validCommand(String email) {
        return new RegisterUserCommand(
            "John", "Doe", LocalDate.of(1990, 1, 1),
            email, "Password123!", "FR"
        );
    }

    @Test
    void register_withValidCommand_persistsUserToDatabase() {
        // Act
        User result = registerUser.execute(validCommand("john@example.com"));

        // Assert
        assertThat(userRepository.findByEmail(new Email("john@example.com"))).isPresent();
        assertThat(result.isEmailVerified()).isFalse();
    }

    @Test
    void verifyEmail_withValidCode_marksUserAsVerifiedInDatabase() {
        // Arrange
        User user = registerUser.execute(validCommand("jane@example.com"));
        String code = findVerificationCode(user.getId().value());

        // Act
        verifyEmail.execute(new VerifyEmailCommand("jane@example.com", code));

        // Assert
        User updated = userRepository.findByEmail(new Email("jane@example.com")).get();
        assertThat(updated.isEmailVerified()).isTrue();
    }

    @Test
    void login_withVerifiedUser_returnsValidToken() {
        // Arrange
        User user = registerUser.execute(validCommand("bob@example.com"));
        String code = findVerificationCode(user.getId().value());
        verifyEmail.execute(new VerifyEmailCommand("bob@example.com", code));

        // Act
        LoginResult result = login.execute(new LoginCommand("bob@example.com", "Password123!"));

        // Assert
        assertThat(result.accessToken()).isNotBlank();
        assertThat(result.email()).isEqualTo("bob@example.com");
    }

    private String findVerificationCode(UUID userId) {
        return jdbcTemplate.queryForObject(
            "SELECT code FROM email_verification_codes WHERE user_id = ? AND used = false",
            String.class,
            userId
        );
}

}
