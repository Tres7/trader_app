package com.traderapp.modules.auth.application.usecases;

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
import com.traderapp.modules.auth.domain.valueObjects.Email;
import org.springframework.stereotype.Service;

@Service
public class VerifyEmail {

    private final UserRepository userRepository;
    private final EmailVerificationCodeRepository emailVerificationCodeRepository;
    private final AuthEventPublisher authEventPublisher;

    public VerifyEmail(
            UserRepository userRepository,
            EmailVerificationCodeRepository emailVerificationCodeRepository,
            AuthEventPublisher authEventPublisher
    ) {
        this.userRepository = userRepository;
        this.emailVerificationCodeRepository = emailVerificationCodeRepository;
        this.authEventPublisher = authEventPublisher;
    }

    public User execute(VerifyEmailCommand command) {
        Email email = new Email(command.email());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.isEmailVerified()) {
            throw new EmailAlreadyVerifiedException("Email is already verified");
        }

        EmailVerificationCode verificationCode = emailVerificationCodeRepository
                .findByUserIdAndCode(user.getId().value(), command.code())
                .orElseThrow(() -> new IllegalArgumentException("Invalid verification code"));


        if (verificationCode.isUsed()) {
            throw new InvalidVerificationCodeException("Verification code has already been used");
        }

        if (verificationCode.isExpired()) {
            throw new ExpiredVerificationCodeException("Verification code has expired");
        }

        verificationCode.markAsUsed();
        user.verifyEmail();

        emailVerificationCodeRepository.save(verificationCode);
        User savedUser = userRepository.save(user);

        UserEmailVerifiedEvent event = new UserEmailVerifiedEvent(
                savedUser.getId().value().toString(),
                savedUser.getEmail().value(),
                savedUser.getFirstName().value()
        );

        authEventPublisher.publishUserEmailVerified(event);

        return savedUser;
    }
}
