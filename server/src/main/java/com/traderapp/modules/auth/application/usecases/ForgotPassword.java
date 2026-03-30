package com.traderapp.modules.auth.application.usecases;

import com.traderapp.modules.auth.application.commands.ForgotPasswordCommand;
import com.traderapp.modules.auth.application.events.PasswordResetRequestedEvent;
import com.traderapp.modules.auth.application.ports.output.AuthEventPublisher;
import com.traderapp.modules.auth.domain.entities.PasswordResetCode;
import com.traderapp.modules.auth.domain.entities.User;
import com.traderapp.modules.auth.domain.exceptions.UserNotFoundException;
import com.traderapp.modules.auth.domain.repositories.PasswordResetCodeRepository;
import com.traderapp.modules.auth.domain.repositories.UserRepository;
import com.traderapp.modules.auth.domain.valueObjects.Email;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class ForgotPassword {

    private final UserRepository userRepository;
    private final PasswordResetCodeRepository passwordResetCodeRepository;
    private final AuthEventPublisher authEventPublisher;
    private final Random random = new Random();

    public ForgotPassword(
        UserRepository userRepository,
        PasswordResetCodeRepository passwordResetCodeRepository,
        AuthEventPublisher authEventPublisher
    ) {
        this.userRepository = userRepository;
        this.passwordResetCodeRepository = passwordResetCodeRepository;
        this.authEventPublisher = authEventPublisher;
    }

    @Transactional
    public User execute(ForgotPasswordCommand command) {
        Email email = new Email(command.email());

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User not found"));

        passwordResetCodeRepository.markAllAsUsedByUserId(user.getId().value());

        String resetCode = generateResetCode();

        PasswordResetCode passwordResetCode = PasswordResetCode.create(
            user.getId().value(),
            resetCode,
            LocalDateTime.now().plusMinutes(15)
        );

        passwordResetCodeRepository.save(passwordResetCode);

        PasswordResetRequestedEvent event = new PasswordResetRequestedEvent(
            user.getId().value().toString(),
            user.getEmail().value(),
            user.getFirstName().value(),
            resetCode
        );

        authEventPublisher.publishPasswordResetRequested(event);

        return user;
    }

    private String generateResetCode() {
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}