package com.wks.servicemarketplace.customerservice.core.auth;

import com.google.common.base.Preconditions;
import com.wks.servicemarketplace.common.auth.Authentication;
import com.wks.servicemarketplace.common.errors.CoreException;
import com.wks.servicemarketplace.common.errors.ErrorType;

public class AuthorizationUtils {

    public static void checkRole(Authentication authentication,
                                 String role) {
        Preconditions.checkNotNull(role);
        Preconditions.checkArgument(!role.isEmpty());

        if (authentication == null) {
            throw new CoreException(ErrorType.AUTHENTICATION, null, null, null);
        }
        if (!authentication.hasRole(role)) {
            throw CoreException.unauthorized(role);
        }
    }
}
