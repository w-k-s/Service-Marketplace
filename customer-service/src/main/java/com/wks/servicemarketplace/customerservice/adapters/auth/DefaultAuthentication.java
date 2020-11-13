package com.wks.servicemarketplace.customerservice.adapters.auth;

import com.wks.servicemarketplace.customerservice.core.auth.Authentication;
import com.wks.servicemarketplace.customerservice.core.auth.User;

import javax.validation.constraints.NotNull;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DefaultAuthentication implements Authentication {
    @NotNull
    private final Optional<User> user;
    @NotNull
    private final String name;
    @NotNull
    private final List<String> permissions;

    @Override
    public String getName() {
        return name;
    }

    public DefaultAuthentication(Builder builder) {
        user = Optional.ofNullable(builder.user);
        name = builder.name;
        permissions = builder.permissions;
    }

    @Override
    public Optional<User> getUser() {
        return user;
    }

    @Override
    public boolean hasRole(String role) {
        return Optional.ofNullable(permissions)
                .orElseGet(Collections::emptyList)
                .stream()
                .anyMatch(it -> it.equalsIgnoreCase(role));
    }

    static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private User user;
        private String name;
        private List<String> permissions;

        private Builder() {
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder permissions(List<String> permissions) {
            this.permissions = permissions;
            return this;
        }

        public DefaultAuthentication build() {
            return new DefaultAuthentication(this);
        }
    }
}
