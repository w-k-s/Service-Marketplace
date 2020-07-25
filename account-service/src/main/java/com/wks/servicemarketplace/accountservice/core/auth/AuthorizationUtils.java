package com.wks.servicemarketplace.accountservice.core.auth;

import com.google.common.base.Preconditions;
import com.wks.servicemarketplace.accountservice.core.exceptions.AuthenticationRequiredException;
import com.wks.servicemarketplace.accountservice.core.exceptions.UnauthorizedException;

public class AuthorizationUtils {

    public static void checkRole(User user,
                                 String role) {
        Preconditions.checkNotNull(role);
        Preconditions.checkArgument(!role.isEmpty());

        if (user == null) {
            throw new AuthenticationRequiredException();
        }
        if (!user.hasRole(role)) {
            throw UnauthorizedException.requiredRole(role);
        }
    }
}
