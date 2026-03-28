package com.traderapp.modules.auth.application.usecases;

import java.time.LocalDateTime;

import com.traderapp.modules.auth.application.commands.RegisterUserCommand;
import com.traderapp.modules.auth.application.service.PasswordHasher;
import com.traderapp.modules.auth.domain.entities.User;
import com.traderapp.modules.auth.domain.repositories.UserRepository;
import com.traderapp.modules.auth.domain.valueObjects.BirthDate;
import com.traderapp.modules.auth.domain.valueObjects.Country;
import com.traderapp.modules.auth.domain.valueObjects.Email;
import com.traderapp.modules.auth.domain.valueObjects.FirstName;
import com.traderapp.modules.auth.domain.valueObjects.LastName;
import com.traderapp.modules.auth.domain.valueObjects.PasswordHash;
import com.traderapp.modules.auth.domain.valueObjects.UserId;

public class RegisterUser {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public RegisterUser (UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
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

        return userRepository.save(user);
    }
}
