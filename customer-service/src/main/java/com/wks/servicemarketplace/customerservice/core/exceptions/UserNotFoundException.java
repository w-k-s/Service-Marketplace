package com.wks.servicemarketplace.customerservice.core.exceptions;

public class UserNotFoundException extends CoreException {
    public UserNotFoundException() {
        super(ErrorType.NOT_FOUND, "Customer not found");
    }
}
