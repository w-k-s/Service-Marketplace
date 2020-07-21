package com.wks.servicemarketplace.accountservice.core.auth;

public interface User {
    String getUuid();

    String getUsername();

    boolean hasRole(String role);
}
