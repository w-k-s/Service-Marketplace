package com.wks.servicemarketplace.customerservice.core.exceptions;

public class AuthenticationRequiredException extends RuntimeException {
    public AuthenticationRequiredException() {
        super("User must be authenticated to perform this request");
    }
}
