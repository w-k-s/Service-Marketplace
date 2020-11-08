package com.wks.servicemarketplace.customerservice.adapters.auth;

import com.wks.servicemarketplace.customerservice.core.auth.Authentication;

public interface TokenValidator {
    Authentication authenticate(String token) throws InvalidTokenException;
}
