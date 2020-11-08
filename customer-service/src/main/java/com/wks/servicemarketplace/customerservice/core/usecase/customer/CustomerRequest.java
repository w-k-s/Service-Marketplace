package com.wks.servicemarketplace.customerservice.core.usecase.customer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.wks.servicemarketplace.customerservice.core.utils.ModelValidator;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@JsonDeserialize(builder = CustomerRequest.Builder.class)
public class CustomerRequest {

    @NotBlank
    @Size(min = 2, max = 50)
    private final String firstName;

    @NotBlank
    @Size(min = 2, max = 50)
    private final String lastName;

    @NotBlank
    @Email
    private final String email;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    private CustomerRequest(Builder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
    }

    private static CustomerRequest create(Builder builder) {
        return ModelValidator.validate(new CustomerRequest(builder));
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
        private String firstName;
        private String lastName;
        private String email;

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public CustomerRequest build() {
            return CustomerRequest.create(this);
        }
    }
}