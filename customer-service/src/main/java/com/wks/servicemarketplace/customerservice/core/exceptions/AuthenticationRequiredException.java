package com.wks.servicemarketplace.customerservice.core.exceptions;

public class AuthenticationRequiredException extends CoreException {
    public AuthenticationRequiredException() {
        super(ErrorType.UNAUTHENTICATED, "User must be authenticated to perform this request");
    }
}
