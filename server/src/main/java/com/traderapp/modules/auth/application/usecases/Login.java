package com.traderapp.modules.auth.application.usecases;

import org.springframework.stereotype.Service;

import com.traderapp.modules.auth.application.commands.LoginCommand;
import com.traderapp.modules.auth.application.dto.LoginResult;
import com.traderapp.modules.auth.application.service.JwtService;
import com.traderapp.modules.auth.application.service.PasswordVerifier;
import com.traderapp.modules.auth.domain.entities.User;
import com.traderapp.modules.auth.domain.exceptions.EmailNotVerifiedException;
import com.traderapp.modules.auth.domain.exceptions.InvalidCredentialsException;
import com.traderapp.modules.auth.domain.repositories.UserRepository;
import com.traderapp.modules.auth.domain.valueObjects.Email;

@Service
public class Login {
    private final UserRepository userRepository;
    private final PasswordVerifier passwordVerifier;
    private final JwtService jwtService;

    public Login(UserRepository userRepository, PasswordVerifier passwordVerifier, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordVerifier = passwordVerifier;
        this.jwtService = jwtService;
    }
    
    public LoginResult execute(LoginCommand command) {
        Email email = new Email(command.email());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        boolean passwordMatches = passwordVerifier.matches(
                command.password(),
                user.getPasswordHash()
        );

        if (!passwordMatches) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (!user.isEmailVerified()) {
            throw new EmailNotVerifiedException("Email must be verified before login");
        }

        String accessToken = jwtService.generateAccessToken(user);

        return new LoginResult(
            accessToken,
            user.getId().value().toString(),
            user.getEmail().value(),
            user.getFirstName().value()
        );
    }
    
}

