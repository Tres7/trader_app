package com.traderapp.modules.auth.domain.entities;

import java.time.LocalDateTime;
import java.util.Objects;

import com.traderapp.modules.auth.domain.valueObjects.BirthDate;
import com.traderapp.modules.auth.domain.valueObjects.Country;
import com.traderapp.modules.auth.domain.valueObjects.Email;
import com.traderapp.modules.auth.domain.valueObjects.FirstName;
import com.traderapp.modules.auth.domain.valueObjects.LastName;
import com.traderapp.modules.auth.domain.valueObjects.PasswordHash;
import com.traderapp.modules.auth.domain.valueObjects.UserId;

public class User {
    private UserId id;
    private LastName lastName;
    private FirstName firstName;
    private BirthDate birthDate;
    private Country country;
    private Email email;
    private PasswordHash passwordHash;
    private boolean emailVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User (UserId id,
            FirstName firstName,
            LastName lastName,
            BirthDate birthDate,
            Email email,
            PasswordHash passwordHash,
            Country country,
            boolean emailVerified,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.email = email;
        this.passwordHash = passwordHash;
        this.country = country;
        this.emailVerified = emailVerified;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        
    }

    public void verifyEmail() {
        this.emailVerified = true;
        touch();
    }

        public void changePassword(PasswordHash newPasswordHash) {
        this.passwordHash = Objects.requireNonNull(newPasswordHash, "Password hash cannot be null");
        touch();
    }

    public void updateProfile(
        FirstName newFirstName,
        LastName newLastName,
        BirthDate newBirthDate,
        Country newCountry
    ) {
        this.firstName = Objects.requireNonNull(newFirstName, "First name cannot be null");
        this.lastName = Objects.requireNonNull(newLastName, "Last name cannot be null");
        this.birthDate = Objects.requireNonNull(newBirthDate, "Birth date cannot be null");
        this.country = Objects.requireNonNull(newCountry, "Country cannot be null");
        touch();
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }


    public UserId getId() {
        return id;
    }

    public FirstName getFirstName() {
        return firstName;
    }

    public LastName getLastName() {
        return lastName;
    }

    public BirthDate getBirthDate() {
        return birthDate;
    }

    public Email getEmail() {
        return email;
    }

    public PasswordHash getPasswordHash() {
        return passwordHash;
    }

    public Country getCountry() {
        return country;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}