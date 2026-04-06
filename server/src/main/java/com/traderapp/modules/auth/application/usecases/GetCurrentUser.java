package com.traderapp.modules.auth.application.usecases;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.traderapp.modules.auth.domain.entities.User;
import com.traderapp.modules.auth.domain.exceptions.UserNotFoundException;
import com.traderapp.modules.auth.domain.repositories.UserRepository;
import com.traderapp.modules.auth.domain.valueObjects.UserId;

@Service
public class GetCurrentUser {
    private final UserRepository userRepository;

    public GetCurrentUser(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(String userId) {
        return userRepository.findById(new UserId(UUID.fromString(userId)))
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
