package com.wks.servicemarketplace.customerservice.core.auth;

import com.google.common.base.Preconditions;
import com.wks.servicemarketplace.common.auth.Authentication;
import com.wks.servicemarketplace.common.errors.AuthenticationRequiredException;
import com.wks.servicemarketplace.common.errors.UnauthorizedException;

public class AuthorizationUtils {

    public static void checkRole(Authentication authentication,
                                 String role) {
        Preconditions.checkNotNull(role);
        Preconditions.checkArgument(!role.isEmpty());

        if (authentication == null) {
            throw new AuthenticationRequiredException();
        }
        if (!authentication.hasRole(role)) {
            throw UnauthorizedException.withMissingPermission(role);
        }
    }
}
