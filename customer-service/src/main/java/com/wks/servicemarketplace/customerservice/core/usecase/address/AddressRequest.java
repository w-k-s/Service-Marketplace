package com.wks.servicemarketplace.customerservice.core.usecase.address;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.wks.servicemarketplace.customerservice.core.auth.Authentication;
import com.wks.servicemarketplace.customerservice.core.utils.ModelValidator;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@JsonDeserialize(builder = AddressRequest.Builder.class)
public class AddressRequest {

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
    private final Authentication authentication;

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

    public Authentication getAuthentication() {
        return authentication;
    }

    private AddressRequest(Builder builder) {
        this.name = builder.name;
        this.line1 = builder.line1;
        this.line2 = builder.line2;
        this.city = builder.city;
        this.country = builder.country;
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
        this.authentication = builder.authentication;
    }

    private static AddressRequest create(Builder builder) {
        return ModelValidator.validate(new AddressRequest(builder));
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
        @JsonProperty("name")
        private String name;
        @JsonProperty("line1")
        private String line1;
        @JsonProperty("line2")
        private String line2;
        @JsonProperty("city")
        private String city;
        @JsonProperty("country")
        private String country;
        @JsonProperty("latitude")
        private BigDecimal latitude;
        @JsonProperty("longitude")
        private BigDecimal longitude;
        private Authentication authentication;

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

        public Builder authentication(Authentication authentication) {
            this.authentication = authentication;
            return this;
        }

        public AddressRequest build() {
            return AddressRequest.create(this);
        }
    }
}
