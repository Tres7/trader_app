package com.traderapp.modules.auth.infrastructure.persistence.repositories;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.traderapp.modules.auth.domain.entities.User;
import com.traderapp.modules.auth.domain.repositories.UserRepository;
import com.traderapp.modules.auth.domain.valueObjects.BirthDate;
import com.traderapp.modules.auth.domain.valueObjects.Country;
import com.traderapp.modules.auth.domain.valueObjects.Email;
import com.traderapp.modules.auth.domain.valueObjects.FirstName;
import com.traderapp.modules.auth.domain.valueObjects.LastName;
import com.traderapp.modules.auth.domain.valueObjects.PasswordHash;
import com.traderapp.modules.auth.domain.valueObjects.UserId;
import com.traderapp.modules.auth.infrastructure.persistence.entities.UserEntity;

@Repository
public class JpaUserRepository implements UserRepository  {
    private final SpringDataUserRepository springDataUserRepository;

    public JpaUserRepository(SpringDataUserRepository springDataUserRepository) {
        this.springDataUserRepository = springDataUserRepository;
    }

    @Override
    public User save(User user) {
        UserEntity userEntity = toEntity(user);
        UserEntity savedUserEntity = springDataUserRepository.save(userEntity);
        return toDomain(savedUserEntity);
    }

    @Override
    public Optional<User> findById(UserId userId) {
        return springDataUserRepository.findById(userId.value())
                .map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return springDataUserRepository.findByEmail(email.value())
                .map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return springDataUserRepository.existsByEmail(email.value());
    }


    private UserEntity toEntity(User user) {
        return new UserEntity(
                user.getId().value(),
                user.getFirstName().value(),
                user.getLastName().value(),
                user.getBirthDate().value(),
                user.getEmail().value(),
                user.getPasswordHash().value(),
                user.getCountry().value(),
                user.isEmailVerified(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    private User toDomain(UserEntity userEntity) {
        return new User(
                new UserId(userEntity.getId()),
                new FirstName(userEntity.getFirstName()),
                new LastName(userEntity.getLastName()),
                new BirthDate(userEntity.getBirthDate()),
                new Email(userEntity.getEmail()),
                new PasswordHash(userEntity.getPasswordHash()),
                new Country(userEntity.getCountry()),
                userEntity.isEmailVerified(),
                userEntity.getCreatedAt(),
                userEntity.getUpdatedAt()
        );
    }
}
