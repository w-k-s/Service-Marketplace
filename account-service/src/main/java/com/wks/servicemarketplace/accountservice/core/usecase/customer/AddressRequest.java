package com.wks.servicemarketplace.accountservice.core.usecase.customer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Value
@JsonDeserialize(builder = AddressRequest.Builder.class)
@Builder(builderClassName = "Builder", toBuilder = true)
public class AddressRequest {

    /**
     * TODO: Remove, customerExternalId will be from token
     */
    @NonNull
    @NotNull
    @PositiveOrZero
    private final Long customerExternalId;

    @NonNull
    @NotBlank
    @Size(min = 2, max = 50)
    private final String name;

    @NonNull
    @NotBlank
    @Size(min = 2, max = 100)
    private final String line1;

    @Size(max = 100)
    private final String line2;

    @NonNull
    @NotBlank
    @Size(min = 2, max = 60)
    private final String city;

    @NotNull
    @NotBlank
    @Size(min = 2, max = 2)
    private final String country;

    @NonNull
    @NotNull
    @DecimalMin("-90")
    @DecimalMax("90")
    private final BigDecimal latitude;

    @NonNull
    @NotNull
    @DecimalMin("-180")
    @DecimalMax("180")
    private final BigDecimal longitude;

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
    }
}
