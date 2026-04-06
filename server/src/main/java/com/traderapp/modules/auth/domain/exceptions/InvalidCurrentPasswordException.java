package com.traderapp.modules.auth.domain.exceptions;

public class InvalidCurrentPasswordException extends RuntimeException {
    
    public InvalidCurrentPasswordException(String message) {
        super(message);
    }
    
}
