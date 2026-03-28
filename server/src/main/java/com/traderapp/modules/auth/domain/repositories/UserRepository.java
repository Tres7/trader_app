package com.traderapp.modules.auth.domain.repositories;

import java.util.Optional;

import com.traderapp.modules.auth.domain.entities.User;
import com.traderapp.modules.auth.domain.valueObjects.Email;
import com.traderapp.modules.auth.domain.valueObjects.UserId;

public interface UserRepository {
    User save (User user);
    Optional<User> findById(UserId userId);
    Optional<User> findByEmail(Email email);
    boolean existsByEmail(Email email);
}
