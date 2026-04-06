package com.traderapp.modules.auth.application.usecases;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.traderapp.modules.auth.application.commands.ResendVerificationCodeCommand;
import com.traderapp.modules.auth.application.events.UserRegisteredEvent;
import com.traderapp.modules.auth.application.ports.output.AuthEventPublisher;
import com.traderapp.modules.auth.domain.entities.EmailVerificationCode;
import com.traderapp.modules.auth.domain.entities.User;
import com.traderapp.modules.auth.domain.exceptions.EmailAlreadyVerifiedException;
import com.traderapp.modules.auth.domain.exceptions.UserNotFoundException;
import com.traderapp.modules.auth.domain.repositories.EmailVerificationCodeRepository;
import com.traderapp.modules.auth.domain.repositories.UserRepository;
import com.traderapp.modules.auth.domain.valueObjects.Email;

@Service
public class ResendVerificationCode {
    private final UserRepository userRepository;
    private final EmailVerificationCodeRepository emailVerificationCodeRepository;
    private final AuthEventPublisher authEventPublisher;
    private final Random random = new Random();

    public ResendVerificationCode(
       UserRepository userRepository,
       EmailVerificationCodeRepository emailVerificationCodeRepository,
       AuthEventPublisher authEventPublisher
    ) {
       this.userRepository = userRepository;
       this.emailVerificationCodeRepository = emailVerificationCodeRepository;
       this.authEventPublisher = authEventPublisher;
    }

    public User execute(ResendVerificationCodeCommand command) {
        Email email = new Email(command.email());
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.isEmailVerified()) {
            throw new EmailAlreadyVerifiedException("Email is already verified");
        }

        emailVerificationCodeRepository.markAllAsUsedByUserId(user.getId().value());

        String verificationCode = generateVerificationCode();
        
                EmailVerificationCode newCode = EmailVerificationCode.create(
                user.getId().value(),
                verificationCode,
                LocalDateTime.now().plusMinutes(15)
        );

        emailVerificationCodeRepository.save(newCode);
            UserRegisteredEvent event = new UserRegisteredEvent(
            user.getId().value().toString(),
            user.getEmail().value(),
            user.getFirstName().value(),
            verificationCode
        );
        
        authEventPublisher.publishUserRegistered(event);
        return user;
        
    }

    private String generateVerificationCode() {
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
