package com.wks.servicemarketplace.accountservice.adapters.auth;

import com.wks.servicemarketplace.accountservice.core.auth.User;

public interface TokenValidator {
    User getUserIfValid(String token);
}
