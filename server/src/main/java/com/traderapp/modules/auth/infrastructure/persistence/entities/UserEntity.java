package com.traderapp.modules.auth.infrastructure.persistence.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    private UUID id;

    @Column(name="first_name", nullable = false)
    private String firstName;

    @Column(name="last_name", nullable = false)
    private String lastName;

    @Column(name="birthDate", nullable = false)
    private LocalDate birthDate;

    @Column(name="country", nullable = false)
    private String country;

    @Column(name="email", nullable = false, unique = true)
    private String email;
   
    @Column(name="password_hash", nullable = false)
    private String passwordHash;
   
    @Column(name="email_verified", nullable = false)
    private boolean emailVerified;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected UserEntity() {
    }

    public UserEntity(
            UUID id,
            String firstName,
            String lastName,
            LocalDate birthDate,
            String email,
            String passwordHash,
            String country,
            boolean emailVerified,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
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

    public UUID getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getCountry() {
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
