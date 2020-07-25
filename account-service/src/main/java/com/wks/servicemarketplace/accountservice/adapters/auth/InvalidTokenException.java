package com.wks.servicemarketplace.accountservice.adapters.auth;

public class InvalidTokenException extends Exception {

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
