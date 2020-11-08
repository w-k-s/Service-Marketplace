package com.wks.servicemarketplace.customerservice.core.auth;

import java.security.Principal;
import java.util.Optional;

public interface Authentication extends Principal {
    Optional<Principal> getUser();

    boolean hasRole(String role);
}
