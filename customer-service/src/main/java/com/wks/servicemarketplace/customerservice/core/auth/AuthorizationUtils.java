package com.wks.servicemarketplace.customerservice.core.auth;

import com.google.common.base.Preconditions;
import com.wks.servicemarketplace.customerservice.core.exceptions.AuthenticationRequiredException;
import com.wks.servicemarketplace.customerservice.core.exceptions.UnauthorizedException;

public class AuthorizationUtils {

    public static void checkRole(Authentication authentication,
                                 String role) {
        Preconditions.checkNotNull(role);
        Preconditions.checkArgument(!role.isEmpty());

        if (authentication == null) {
            throw new AuthenticationRequiredException();
        }
        if (!authentication.hasRole(role)) {
            throw UnauthorizedException.requiredRole(role);
        }
    }
}
