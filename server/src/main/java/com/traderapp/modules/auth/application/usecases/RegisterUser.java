package com.traderapp.modules.auth.application.usecases;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.traderapp.modules.auth.application.commands.RegisterUserCommand;
import com.traderapp.modules.auth.application.events.UserRegisteredEvent;
import com.traderapp.modules.auth.application.ports.output.AuthEventPublisher;
import com.traderapp.modules.auth.application.service.PasswordHasher;
import com.traderapp.modules.auth.domain.entities.EmailVerificationCode;
import com.traderapp.modules.auth.domain.entities.User;
import com.traderapp.modules.auth.domain.repositories.EmailVerificationCodeRepository;
import com.traderapp.modules.auth.domain.repositories.UserRepository;
import com.traderapp.modules.auth.domain.valueObjects.BirthDate;
import com.traderapp.modules.auth.domain.valueObjects.Country;
import com.traderapp.modules.auth.domain.valueObjects.Email;
import com.traderapp.modules.auth.domain.valueObjects.FirstName;
import com.traderapp.modules.auth.domain.valueObjects.LastName;
import com.traderapp.modules.auth.domain.valueObjects.PasswordHash;
import com.traderapp.modules.auth.domain.valueObjects.UserId;
import java.util.Random;

@Service
public class RegisterUser {
    private final UserRepository userRepository;
    EmailVerificationCodeRepository emailVerificationCodeRepository;
    private final PasswordHasher passwordHasher;
    private final AuthEventPublisher authEventPublisher;
    private final Random random = new Random();

    public RegisterUser (UserRepository userRepository, PasswordHasher passwordHasher, AuthEventPublisher authEventPublisher, EmailVerificationCodeRepository emailVerificationCodeRepository) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.authEventPublisher = authEventPublisher;
        this.emailVerificationCodeRepository = emailVerificationCodeRepository;
    }

    public User execute(RegisterUserCommand command) {
        Email email = new Email(command.email());

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("A user with this email already exists");
        }

        PasswordHash passwordHash = passwordHasher.hash(command.rawPassword());

        User user = new User(
                UserId.generate(),
                new FirstName(command.firstName()),
                new LastName(command.lastName()),
                new BirthDate(command.birthDate()),
                email,
                passwordHash,
                new Country(command.country()),
                false,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        
        User savedUser = userRepository.save(user);

        String verificationCode = generateVerificationCode();

        EmailVerificationCode emailVerificationCode = EmailVerificationCode.create(
            savedUser.getId().value(),
            verificationCode,
            LocalDateTime.now().plusMinutes(15)
        );
        emailVerificationCodeRepository.save(emailVerificationCode);
        
        UserRegisteredEvent event = new UserRegisteredEvent(
            savedUser.getId().value().toString(),
            savedUser.getEmail().value(),
            savedUser.getFirstName().value(),
            verificationCode
        );
        
        authEventPublisher.publishUserRegistered(event);
        return savedUser;
    }

    private String generateVerificationCode() {
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

}
