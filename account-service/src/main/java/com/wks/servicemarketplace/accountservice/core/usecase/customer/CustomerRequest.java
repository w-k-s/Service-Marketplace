package com.wks.servicemarketplace.accountservice.core.usecase.customer;

import com.wks.servicemarketplace.accountservice.core.auth.User;
import com.wks.servicemarketplace.accountservice.core.utils.ModelValidator;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CustomerRequest {

    @NotBlank
    @Size(min = 2, max = 50)
    private final String firstName;

    @NotBlank
    @Size(min = 2, max = 50)
    private final String lastName;

    @NotNull
    private final User user;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public User getUser() {
        return user;
    }

    private CustomerRequest(Builder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.user = builder.user;
    }

    private static CustomerRequest create(Builder builder) {
        return ModelValidator.validate(new CustomerRequest(builder), CustomerRequest.class);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String firstName;
        private String lastName;
        private User user;

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = firstName;
            return this;
        }

        public CustomerRequest build() {
            return CustomerRequest.create(this);
        }
    }
}