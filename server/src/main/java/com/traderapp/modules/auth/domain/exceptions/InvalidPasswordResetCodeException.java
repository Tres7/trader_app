package com.traderapp.modules.auth.domain.exceptions;

public class InvalidPasswordResetCodeException extends RuntimeException {
    
    public InvalidPasswordResetCodeException(String message) {
        super(message);
    }
}
