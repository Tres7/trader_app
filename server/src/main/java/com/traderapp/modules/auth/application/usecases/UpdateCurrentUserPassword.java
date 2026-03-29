package com.traderapp.modules.auth.application.usecases;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.traderapp.modules.auth.application.service.PasswordHasher;
import com.traderapp.modules.auth.application.service.PasswordVerifier;
import com.traderapp.modules.auth.domain.entities.User;
import com.traderapp.modules.auth.domain.exceptions.InvalidCurrentPasswordException;
import com.traderapp.modules.auth.domain.exceptions.UserNotFoundException;
import com.traderapp.modules.auth.domain.repositories.UserRepository;
import com.traderapp.modules.auth.domain.valueObjects.PasswordHash;
import com.traderapp.modules.auth.domain.valueObjects.UserId;

@Service
public class UpdateCurrentUserPassword {
    private final UserRepository userRepository;
    private final PasswordVerifier passwordVerifier;
    private final PasswordHasher passwordHasher;

    public UpdateCurrentUserPassword(UserRepository userRepository, PasswordVerifier passwordVerifier, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordVerifier = passwordVerifier;
        this.passwordHasher = passwordHasher;
    }
    
    public void execute(
        String userId,
        String currentPassword,
        String newPassword
    ) {
        User user = userRepository.findById(new UserId(UUID.fromString(userId)))
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        boolean currentPasswordMatches = passwordVerifier.matches(
                currentPassword,
                user.getPasswordHash()
        );

        if (!currentPasswordMatches) {
            throw new InvalidCurrentPasswordException("Current password is incorrect");
        }

        PasswordHash newPasswordHash = passwordHasher.hash(newPassword);

        user.changePassword(newPasswordHash);

        userRepository.save(user);
    }
    
}
