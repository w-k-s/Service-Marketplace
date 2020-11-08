package com.wks.servicemarketplace.customerservice.adapters.auth;

import javax.security.auth.Subject;
import java.security.Principal;

public class DefaultUser implements Principal {

    private final String id;
    private final String firstName;
    private final String lastName;
    private final String username;
    private final String email;
    private final String role;

    public DefaultUser(Builder builder) {
        id = builder.id;
        username = builder.username;
        firstName = builder.firstName;
        role = builder.role;
        lastName = builder.lastName;
        email = builder.email;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String getName() {
        return username;
    }

    @Override
    public boolean implies(Subject subject) {
        return false;
    }

    public static final class Builder {
        private String id;
        private String firstName;
        private String lastName;
        private String username;
        private String email;
        private String role;

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public DefaultUser build() {
            return new DefaultUser(this);
        }
    }
}
