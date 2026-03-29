package com.traderapp.modules.auth.domain.exceptions;

public class ExpiredVerificationCodeException extends RuntimeException {

    public ExpiredVerificationCodeException(String message) {
        super(message);
    }
}