package com.traderapp.modules.auth.application.usecases;

import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.traderapp.modules.auth.application.commands.RegisterUserCommand;
import com.traderapp.modules.auth.application.ports.output.AuthEventPublisher;
import com.traderapp.modules.auth.application.service.PasswordHasher;
import com.traderapp.modules.auth.domain.entities.EmailVerificationCode;
import com.traderapp.modules.auth.domain.entities.User;
import com.traderapp.modules.auth.domain.repositories.EmailVerificationCodeRepository;
import com.traderapp.modules.auth.domain.repositories.UserRepository;
import com.traderapp.modules.auth.domain.valueObjects.PasswordHash;

import com.traderapp.modules.auth.domain.valueObjects.Email;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegisterUserTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    PasswordHasher passwordHasher;

    @Mock
    AuthEventPublisher authEventPublisher;

    @Mock EmailVerificationCodeRepository emailVerificationCodeRepository;

    @InjectMocks RegisterUser registerUser;

    private RegisterUserCommand validCommand() {
        return new RegisterUserCommand(
            "Jean", "Dupont", LocalDate.of(1990,1,1),
            "jean@test.com", "password123", "FR"
        );
    }

    @Test
    void execute_withValidEmail_savesUserAndPublishesEvent() {
        //Arrange
        RegisterUserCommand command = validCommand();
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(passwordHasher.hash(any())).thenReturn(new PasswordHash("$2a$10$stubbedHashForUnitTests12"));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        //Act 
        User result = registerUser.execute(command);

        //Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmail().value()).isEqualTo("jean@test.com");
        verify(userRepository).save(any(User.class));
        verify(emailVerificationCodeRepository).save(any(EmailVerificationCode.class));
        verify(authEventPublisher).publishUserRegistered(any());
    }

    @Test
    void execute_withAlreadyExistingEmail_throwIllegalArgumentException() {
        RegisterUserCommand command = validCommand();
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(true);

        assertThatThrownBy(() -> registerUser.execute(command))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("A user with this email already exists");

        verify(userRepository, never()).save(any());
        verify(authEventPublisher, never()).publishUserRegistered(any());
    }
}
