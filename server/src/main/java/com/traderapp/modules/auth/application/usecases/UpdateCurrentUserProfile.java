package com.traderapp.modules.auth.application.usecases;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.traderapp.modules.auth.domain.entities.User;
import com.traderapp.modules.auth.domain.exceptions.UserNotFoundException;
import com.traderapp.modules.auth.domain.repositories.UserRepository;
import com.traderapp.modules.auth.domain.valueObjects.BirthDate;
import com.traderapp.modules.auth.domain.valueObjects.Country;
import com.traderapp.modules.auth.domain.valueObjects.FirstName;
import com.traderapp.modules.auth.domain.valueObjects.LastName;
import com.traderapp.modules.auth.domain.valueObjects.UserId;

@Service
public class UpdateCurrentUserProfile {
    private final UserRepository userRepository;

    public UpdateCurrentUserProfile(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(
            String userId,
            String firstName,
            String lastName,
            LocalDate birthDate,
            String country
    ) {
        User user = userRepository.findById(new UserId(UUID.fromString(userId)))
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.updateProfile(
                new FirstName(firstName),
                new LastName(lastName),
                new BirthDate(birthDate),
                new Country(country)
        );
        return userRepository.save(user);
    }
}
