package com.traderapp.shared.infrastructure.http;

import com.traderapp.modules.auth.domain.exceptions.EmailAlreadyVerifiedException;
import com.traderapp.modules.auth.domain.exceptions.EmailNotVerifiedException;
import com.traderapp.modules.auth.domain.exceptions.ExpiredVerificationCodeException;
import com.traderapp.modules.auth.domain.exceptions.InvalidCredentialsException;
import com.traderapp.modules.auth.domain.exceptions.InvalidCurrentPasswordException;
import com.traderapp.modules.auth.domain.exceptions.InvalidVerificationCodeException;
import com.traderapp.modules.auth.domain.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFound(UserNotFoundException exception) {
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(InvalidVerificationCodeException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidVerificationCode(InvalidVerificationCodeException exception) {
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(ExpiredVerificationCodeException.class)
    public ResponseEntity<ApiErrorResponse> handleExpiredVerificationCode(ExpiredVerificationCodeException exception) {
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(EmailAlreadyVerifiedException.class)
    public ResponseEntity<ApiErrorResponse> handleEmailAlreadyVerified(EmailAlreadyVerifiedException exception) {
        return buildResponse(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException exception) {
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidCredentials(InvalidCredentialsException exception) {
        return buildResponse(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<ApiErrorResponse> handleEmailNotVerified(EmailNotVerifiedException exception) {
        return buildResponse(HttpStatus.FORBIDDEN, exception.getMessage());
    }

    @ExceptionHandler(InvalidCurrentPasswordException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidCurrentPassword(InvalidCurrentPasswordException exception) {
        return buildResponse(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }


    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String message) {
        ApiErrorResponse response = new ApiErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                LocalDateTime.now()
        );

        return ResponseEntity.status(status).body(response);
    }
}