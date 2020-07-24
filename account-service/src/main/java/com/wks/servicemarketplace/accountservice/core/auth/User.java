package com.wks.servicemarketplace.accountservice.core.auth;

import java.security.Principal;

public interface User extends Principal {

    String getUuid();

    String getUsername();

    boolean hasRole(String role);
}
