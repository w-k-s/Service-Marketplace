package com.wks.servicemarketplace.accountservice.core.auth;

import com.google.common.base.Preconditions;
import com.wks.servicemarketplace.accountservice.core.exceptions.AuthenticationRequiredException;
import com.wks.servicemarketplace.accountservice.core.exceptions.UnauthorizedException;

public class AuthorizationUtils {

    public static void checkRole(UserProvider userProvider,
                                 String role) {
        Preconditions.checkNotNull(role);
        Preconditions.checkArgument(!role.isEmpty());

        if (userProvider == null || userProvider.getUser() == null) {
            throw new AuthenticationRequiredException();
        }
        if (!userProvider.getUser().hasRole(role)) {
            throw UnauthorizedException.requiredRole(role);
        }
    }
}
