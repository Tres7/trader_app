package com.traderapp.modules.auth.infrastructure.http.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.traderapp.modules.auth.application.commands.RegisterUserCommand;
import com.traderapp.modules.auth.application.usecases.RegisterUser;
import com.traderapp.modules.auth.domain.entities.User;
import com.traderapp.modules.auth.infrastructure.http.responses.RegisterUserResponse;
import com.traderapp.modules.auth.presentation.rest.requests.RegisterUserRequest;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final RegisterUser registerUser;

    public AuthController(RegisterUser registerUser) {
        this.registerUser = registerUser;
    }

     @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(@RequestBody RegisterUserRequest request) {
        RegisterUserCommand command = new RegisterUserCommand(
                request.firstName(),
                request.lastName(),
                request.birthDate(),
                request.email(),
                request.password(),
                request.country()
        );

        User user = registerUser.execute(command);

         RegisterUserResponse response = new RegisterUserResponse(
                user.getId().value().toString(),
                user.getEmail().value(),
                user.isEmailVerified(),
                "User registered successfully. Email sent"
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    
}
