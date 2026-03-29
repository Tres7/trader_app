package com.traderapp.modules.auth.infrastructure.http.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.traderapp.modules.auth.application.commands.LoginCommand;
import com.traderapp.modules.auth.application.commands.RegisterUserCommand;
import com.traderapp.modules.auth.application.commands.ResendVerificationCodeCommand;
import com.traderapp.modules.auth.application.commands.VerifyEmailCommand;
import com.traderapp.modules.auth.application.dto.AuthenticatedUser;
import com.traderapp.modules.auth.application.dto.LoginResult;
import com.traderapp.modules.auth.application.usecases.GetCurrentUser;
import com.traderapp.modules.auth.application.usecases.Login;
import com.traderapp.modules.auth.application.usecases.RegisterUser;
import com.traderapp.modules.auth.application.usecases.ResendVerificationCode;
import com.traderapp.modules.auth.application.usecases.UpdateCurrentUserProfile;
import com.traderapp.modules.auth.application.usecases.VerifyEmail;
import com.traderapp.modules.auth.domain.entities.User;
import com.traderapp.modules.auth.infrastructure.http.responses.GetCurrentUserResponse;
import com.traderapp.modules.auth.infrastructure.http.responses.RegisterUserResponse;
import com.traderapp.modules.auth.infrastructure.http.responses.ResendVerificationCodeResponse;
import com.traderapp.modules.auth.infrastructure.http.responses.VerifyEmailResponse;
import com.traderapp.modules.auth.presentation.rest.requests.LoginRequest;
import com.traderapp.modules.auth.presentation.rest.requests.LoginResponse;
import com.traderapp.modules.auth.presentation.rest.requests.RegisterUserRequest;
import com.traderapp.modules.auth.presentation.rest.requests.ResendVerificationCodeRequest;
import com.traderapp.modules.auth.presentation.rest.requests.UpdateCurrentUserProfileRequest;
import com.traderapp.modules.auth.presentation.rest.requests.VerifyEmailRequest;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final ResendVerificationCode resendVerificationCode;
    private final RegisterUser registerUser;
    private final VerifyEmail verifyEmail;
    private final Login login;
    private final GetCurrentUser getCurrentUser;
    private final UpdateCurrentUserProfile updateCurrentUserProfile;


    public AuthController(RegisterUser registerUser, VerifyEmail verifyEmail, ResendVerificationCode resendVerificationCode, Login login, GetCurrentUser getCurrentUser, UpdateCurrentUserProfile updateCurrentUserProfile) {
        this.registerUser = registerUser;
        this.verifyEmail = verifyEmail;
        this.resendVerificationCode = resendVerificationCode;
        this.login = login;
        this.getCurrentUser = getCurrentUser;
        this.updateCurrentUserProfile = updateCurrentUserProfile;
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

    @GetMapping("/me")
    public ResponseEntity<GetCurrentUserResponse> me(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        User user = getCurrentUser.execute(authenticatedUser.userId());

        GetCurrentUserResponse response = new GetCurrentUserResponse(
                user.getId().value().toString(),
                user.getFirstName().value(),
                user.getLastName().value(),
                user.getEmail().value(),
                user.getCountry().value(),
                user.getBirthDate().value().toString(),
                user.isEmailVerified()
        );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me")
    public ResponseEntity<GetCurrentUserResponse> updateMe(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestBody UpdateCurrentUserProfileRequest request
    ) {
        User user = updateCurrentUserProfile.execute(
                authenticatedUser.userId(),
                request.firstName(),
                request.lastName(),
                request.birthDate(),
                request.country()
        );

        GetCurrentUserResponse response = new GetCurrentUserResponse(
                user.getId().value().toString(),
                user.getFirstName().value(),
                user.getLastName().value(),
                user.getEmail().value(),
                user.getCountry().value(),
                user.getBirthDate().value().toString(),
                user.isEmailVerified()
        );
        return ResponseEntity.ok(response);
    }
}
