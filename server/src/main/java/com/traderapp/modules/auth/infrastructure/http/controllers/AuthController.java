package com.traderapp.modules.auth.infrastructure.http.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.traderapp.modules.auth.application.commands.RegisterUserCommand;
import com.traderapp.modules.auth.application.commands.VerifyEmailCommand;
import com.traderapp.modules.auth.application.usecases.RegisterUser;
import com.traderapp.modules.auth.application.usecases.VerifyEmail;
import com.traderapp.modules.auth.domain.entities.User;
import com.traderapp.modules.auth.infrastructure.http.responses.RegisterUserResponse;
import com.traderapp.modules.auth.infrastructure.http.responses.VerifyEmailResponse;
import com.traderapp.modules.auth.presentation.rest.requests.RegisterUserRequest;
import com.traderapp.modules.auth.presentation.rest.requests.VerifyEmailRequest;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final RegisterUser registerUser;
    private final VerifyEmail verifyEmail;

    public AuthController(RegisterUser registerUser, VerifyEmail verifyEmail) {
        this.registerUser = registerUser;
        this.verifyEmail = verifyEmail;
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

    @PostMapping("/verify-email")
    public ResponseEntity<VerifyEmailResponse> verifyEmail(@RequestBody VerifyEmailRequest request) {
        VerifyEmailCommand command = new VerifyEmailCommand(
                request.email(),
                request.code()
        );

        User user = verifyEmail.execute(command);

        VerifyEmailResponse response = new VerifyEmailResponse(
                user.getEmail().value(),
                user.isEmailVerified(),
                "Email verified successfully"
        );

        return ResponseEntity.ok(response);
    }
}
