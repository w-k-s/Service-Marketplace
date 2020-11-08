package com.wks.servicemarketplace.customerservice.adapters.auth;

public class InvalidTokenException extends Exception {

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
