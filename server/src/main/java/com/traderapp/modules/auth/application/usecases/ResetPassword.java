package com.traderapp.modules.auth.application.usecases;

import com.traderapp.modules.auth.application.commands.ResetPasswordCommand;
import com.traderapp.modules.auth.application.events.PasswordResetCompletedEvent;
import com.traderapp.modules.auth.application.ports.output.AuthEventPublisher;
import com.traderapp.modules.auth.application.service.PasswordHasher;
import com.traderapp.modules.auth.domain.entities.PasswordResetCode;
import com.traderapp.modules.auth.domain.entities.User;
import com.traderapp.modules.auth.domain.exceptions.ExpiredPasswordResetCodeException;
import com.traderapp.modules.auth.domain.exceptions.InvalidPasswordResetCodeException;
import com.traderapp.modules.auth.domain.exceptions.UserNotFoundException;
import com.traderapp.modules.auth.domain.repositories.PasswordResetCodeRepository;
import com.traderapp.modules.auth.domain.repositories.UserRepository;
import com.traderapp.modules.auth.domain.valueObjects.Email;
import com.traderapp.modules.auth.domain.valueObjects.PasswordHash;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResetPassword {

    private final UserRepository userRepository;
    private final PasswordResetCodeRepository passwordResetCodeRepository;
    private final PasswordHasher passwordHasher;
    private final AuthEventPublisher authEventPublisher;


    public ResetPassword(
            UserRepository userRepository,
            PasswordResetCodeRepository passwordResetCodeRepository,
            PasswordHasher passwordHasher,
            AuthEventPublisher authEventPublisher
    ) {
        this.userRepository = userRepository;
        this.passwordResetCodeRepository = passwordResetCodeRepository;
        this.passwordHasher = passwordHasher;
        this.authEventPublisher = authEventPublisher;
    }

    @Transactional
    public User execute(ResetPasswordCommand command) {
        Email email = new Email(command.email());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        PasswordResetCode resetCode = passwordResetCodeRepository
                .findByUserIdAndCode(user.getId().value(), command.code())
                .orElseThrow(() -> new InvalidPasswordResetCodeException("Invalid password reset code"));

        if (resetCode.isUsed()) {
            throw new InvalidPasswordResetCodeException("Password reset code has already been used");
        }

        if (resetCode.isExpired()) {
            throw new ExpiredPasswordResetCodeException("Password reset code has expired");
        }

        PasswordHash newPasswordHash = passwordHasher.hash(command.newPassword());

        user.changePassword(newPasswordHash);
        resetCode.markAsUsed();

        passwordResetCodeRepository.save(resetCode);
        
        User savedUser = userRepository.save(user);

        PasswordResetCompletedEvent event = new PasswordResetCompletedEvent(
            savedUser.getId().value().toString(),
            savedUser.getEmail().value(),
            savedUser.getFirstName().value()
        );

        authEventPublisher.publishPasswordResetCompleted(event);
        
        return savedUser;
    }
}