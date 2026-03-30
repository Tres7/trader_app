package com.traderapp.modules.auth.domain.exceptions;

public class ExpiredPasswordResetCodeException extends RuntimeException {
    public ExpiredPasswordResetCodeException(String message) {
        super(message);
    }
}
