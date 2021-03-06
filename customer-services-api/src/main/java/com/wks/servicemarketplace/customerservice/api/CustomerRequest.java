package com.wks.servicemarketplace.customerservice.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.wks.servicemarketplace.common.ModelValidator;
import com.wks.servicemarketplace.common.UserId;
import com.wks.servicemarketplace.common.auth.Authentication;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Optional;

@JsonDeserialize(builder = CustomerRequest.Builder.class)
public class CustomerRequest {

    @NotNull
    private final UserId userId;

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
    private final Optional<String> correlationId;

    public UserId getUserId() {
        return userId;
    }

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

    public Optional<String> getCorrelationId() {
        return correlationId;
    }

    private CustomerRequest(Builder builder) {
        this.userId = builder.userId;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
        this.authentication = builder.authentication;
        this.correlationId = Optional.ofNullable(builder.correlationId);
    }

    private static CustomerRequest create(Builder builder) {
        return ModelValidator.validate(new CustomerRequest(builder));
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
        @JsonProperty("userId")
        private UserId userId;
        @JsonProperty("firstName")
        private String firstName;
        @JsonProperty("lastName")
        private String lastName;
        @JsonProperty("email")
        private String email;
        private Authentication authentication;
        private String correlationId;

        public Builder userId(UserId userId) {
            this.userId = userId;
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

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder authentication(Authentication authentication) {
            this.authentication = authentication;
            return this;
        }

        public Builder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public CustomerRequest build() {
            return CustomerRequest.create(this);
        }
    }
}