package com.wks.servicemarketplace.accountservice.adapters.auth;

import com.google.common.base.Preconditions;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

public class User implements Principal, com.wks.servicemarketplace.accountservice.core.auth.User {

    private final String uuid;
    private final String username;
    private final List<String> roles;

    public User(String uuid, String username, List<String> role) {
        Preconditions.checkNotNull(uuid);
        Preconditions.checkArgument(!uuid.isEmpty());

        Preconditions.checkNotNull(username);
        Preconditions.checkArgument(!username.isEmpty());

        this.uuid = uuid;
        this.username = username;
        this.roles = role == null ? Collections.emptyList() : Collections.unmodifiableList(role);
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean hasRole(String role) {
        return roles.stream().anyMatch(it -> it.toLowerCase().equals(role.toLowerCase()));
    }

    @Override
    public String getName() {
        return username;
    }

    @Override
    public String toString() {
        return "User{" +
                "uuid='" + uuid + '\'' +
                ", username='" + username + '\'' +
                ", roles=" + roles +
                '}';
    }
}
