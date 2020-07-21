package com.wks.servicemarketplace.accountservice.core.exceptions;

public class UnauthorizedException extends RuntimeException {

    private final String requiredRole;

    private UnauthorizedException(String requiredRole) {
        super(String.format("User must have role %s to perform this action", requiredRole));
        this.requiredRole = requiredRole;
    }

    public static UnauthorizedException requiredRole(String requiredRole) {
        return new UnauthorizedException(requiredRole);
    }

    public String getRequiredRole() {
        return requiredRole;
    }
}
