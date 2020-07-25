package com.wks.servicemarketplace.accountservice.core.auth;

import com.google.common.base.Preconditions;
import com.wks.servicemarketplace.accountservice.core.exceptions.AuthenticationRequiredException;
import com.wks.servicemarketplace.accountservice.core.exceptions.UnauthorizedException;

import javax.inject.Provider;

public class AuthorizationUtils {

    public static void checkRole(Provider<User> userProvider,
                                 String role) {
        Preconditions.checkNotNull(role);
        Preconditions.checkArgument(!role.isEmpty());

        if (userProvider == null || userProvider.get() == null) {
            throw new AuthenticationRequiredException();
        }
        if (!userProvider.get().hasRole(role)) {
            throw UnauthorizedException.requiredRole(role);
        }
    }
}
