package com.traderapp.modules.auth.infrastructure.http.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.traderapp.modules.auth.application.commands.LoginCommand;
import com.traderapp.modules.auth.application.commands.RegisterUserCommand;
import com.traderapp.modules.auth.application.commands.ResendVerificationCodeCommand;
import com.traderapp.modules.auth.application.commands.VerifyEmailCommand;
import com.traderapp.modules.auth.application.dto.LoginResult;
import com.traderapp.modules.auth.application.usecases.Login;
import com.traderapp.modules.auth.application.usecases.RegisterUser;
import com.traderapp.modules.auth.application.usecases.ResendVerificationCode;
import com.traderapp.modules.auth.application.usecases.VerifyEmail;
import com.traderapp.modules.auth.domain.entities.User;
import com.traderapp.modules.auth.infrastructure.http.responses.RegisterUserResponse;
import com.traderapp.modules.auth.infrastructure.http.responses.ResendVerificationCodeResponse;
import com.traderapp.modules.auth.infrastructure.http.responses.VerifyEmailResponse;
import com.traderapp.modules.auth.presentation.rest.requests.LoginRequest;
import com.traderapp.modules.auth.presentation.rest.requests.LoginResponse;
import com.traderapp.modules.auth.presentation.rest.requests.RegisterUserRequest;
import com.traderapp.modules.auth.presentation.rest.requests.ResendVerificationCodeRequest;
import com.traderapp.modules.auth.presentation.rest.requests.VerifyEmailRequest;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final ResendVerificationCode resendVerificationCode;
    private final RegisterUser registerUser;
    private final VerifyEmail verifyEmail;
    private final Login login;

    public AuthController(RegisterUser registerUser, VerifyEmail verifyEmail, ResendVerificationCode resendVerificationCode, Login login) {
        this.registerUser = registerUser;
        this.verifyEmail = verifyEmail;
        this.resendVerificationCode = resendVerificationCode;
        this.login = login;
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

    @PostMapping("/resend-verification-code")
    public ResponseEntity<ResendVerificationCodeResponse> resendVerificationCode(
            @RequestBody ResendVerificationCodeRequest request) {
        ResendVerificationCodeCommand command = new ResendVerificationCodeCommand(
            request.email()
        );
        User user = resendVerificationCode.execute(command);

        ResendVerificationCodeResponse response = new ResendVerificationCodeResponse(
                user.getEmail().value(),
                "Verification code resent successfully"
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginCommand command = new LoginCommand(
                request.email(),
                request.password()
        );

        LoginResult result = login.execute(command);

        LoginResponse response = new LoginResponse(
                result.accessToken(),
                "Bearer",
                result.userId(),
                result.email(),
                result.firstName()
        );

        return ResponseEntity.ok(response);
    }
}
