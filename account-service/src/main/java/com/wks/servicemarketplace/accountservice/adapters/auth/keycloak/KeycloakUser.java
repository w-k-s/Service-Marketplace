package com.wks.servicemarketplace.accountservice.adapters.auth.keycloak;

import com.wks.servicemarketplace.accountservice.core.auth.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
class KeycloakUser implements User {
    @NotNull
    private final String uuid;
    private final String scope;
    @NotNull
    private final String email;
    private final boolean emailVerified;
    private final String name;
    @NotNull
    private final String preferredUsername;

    private final String givenName;
    private final String familyName;
    @NotNull
    private final List<String> roles;

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUsername() {
        return preferredUsername;
    }

    @Override
    public boolean hasRole(String role) {
        return Optional.ofNullable(roles)
                .orElseGet(Collections::emptyList)
                .stream()
                .anyMatch(it -> it.equalsIgnoreCase(role));
    }
}
