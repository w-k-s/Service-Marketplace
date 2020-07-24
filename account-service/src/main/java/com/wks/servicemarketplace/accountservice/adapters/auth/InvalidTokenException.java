package com.wks.servicemarketplace.accountservice.adapters.auth;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
