package com.wks.servicemarketplace.customerservice.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.wks.servicemarketplace.common.ModelValidator;
import com.wks.servicemarketplace.common.auth.Authentication;
import com.wks.servicemarketplace.common.messaging.Message;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Optional;

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

    @NotNull
    private final Authentication authentication;

    @NotNull
    private final Optional<Message> message;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public Optional<Message> getMessage() {
        return message;
    }

    private CustomerRequest(Builder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
        this.authentication = builder.authentication;
        this.message = Optional.ofNullable(builder.message);
    }

    private static CustomerRequest create(Builder builder) {
        return ModelValidator.validate(new CustomerRequest(builder));
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
        @JsonProperty("firstName")
        private String firstName;
        @JsonProperty("lastName")
        private String lastName;
        @JsonProperty("email")
        private String email;
        private Authentication authentication;
        private Message message;

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

        public Builder authentication(Authentication authentication) {
            this.authentication = authentication;
            return this;
        }

        public Builder message(Message message) {
            this.message = message;
            return this;
        }

        public CustomerRequest build() {
            return CustomerRequest.create(this);
        }
    }
}