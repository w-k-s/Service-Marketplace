package com.wks.servicemarketplace.accountservice.core.usecase.address;

import com.wks.servicemarketplace.accountservice.core.auth.User;
import com.wks.servicemarketplace.accountservice.core.utils.ModelValidator;

import javax.validation.constraints.*;
import java.math.BigDecimal;

public class AddressRequest {

    /**
     * TODO: Remove, customerExternalId will be from token
     */
    @NotNull
    @PositiveOrZero
    private final Long customerExternalId;

    @NotBlank
    @Size(min = 2, max = 50)
    private final String name;

    @NotBlank
    @Size(min = 2, max = 100)
    private final String line1;

    @Size(max = 100)
    private final String line2;

    @NotBlank
    @Size(min = 2, max = 60)
    private final String city;

    @NotNull
    @NotBlank
    @Size(min = 2, max = 2)
    private final String country;

    @NotNull
    @DecimalMin("-90")
    @DecimalMax("90")
    private final BigDecimal latitude;

    @NotNull
    @DecimalMin("-180")
    @DecimalMax("180")
    private final BigDecimal longitude;

    @NotNull
    private final User user;

    public Long getCustomerExternalId() {
        return customerExternalId;
    }

    public String getName() {
        return name;
    }

    public String getLine1() {
        return line1;
    }

    public String getLine2() {
        return line2;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public User getUser() {
        return user;
    }

    private AddressRequest(Builder builder) {
        this.customerExternalId = builder.customerExternalId;
        this.name = builder.name;
        this.line1 = builder.line1;
        this.line2 = builder.line2;
        this.city = builder.city;
        this.country = builder.country;
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
        this.user = builder.user;
    }

    private static AddressRequest create(Builder builder) {
        return ModelValidator.validate(new AddressRequest(builder), AddressRequest.class);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long customerExternalId;
        private String name;
        private String line1;
        private String line2;
        private String city;
        private String country;
        private BigDecimal latitude;
        private BigDecimal longitude;
        private User user;

        public Builder customerExternalId(Long customerExternalId) {
            this.customerExternalId = customerExternalId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder line1(String line1) {
            this.line1 = line1;
            return this;
        }

        public Builder line2(String line2) {
            this.line2 = line2;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder country(String country) {
            this.country = country;
            return this;
        }

        public Builder latitude(BigDecimal latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder longitude(BigDecimal longitude) {
            this.longitude = longitude;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public AddressRequest build() {
            return AddressRequest.create(this);
        }
    }
}
