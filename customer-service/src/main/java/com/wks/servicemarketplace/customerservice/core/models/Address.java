package com.wks.servicemarketplace.customerservice.core.models;

import com.wks.servicemarketplace.customerservice.core.models.events.AddressAddedEvent;
import com.wks.servicemarketplace.customerservice.core.utils.ModelValidator;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.UUID;

@Value
@AllArgsConstructor
public class Address {

    @NonNull
    @NotNull
    @PositiveOrZero
    private final Long externalId;

    @NonNull
    @NotBlank
    private final String uuid;

    @NonNull
    @NotNull
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
    @NonNull
    private final CountryCode country;

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

    private final ZonedDateTime createdDate;

    private final String createdBy;

    private final ZonedDateTime lastModifiedDate;

    private final String lastModifiedBy;

    @NonNull
    private final Long version;

    public BigDecimal getLatitude() {
        return this.latitude.setScale(5, RoundingMode.HALF_UP);
    }

    public BigDecimal getLongitude() {
        return this.longitude.setScale(5, RoundingMode.HALF_UP);
    }

    public static ResultWithEvents<Address, AddressAddedEvent> create(long externalId,
                                                                      long customerExternalId,
                                                                      String addressName,
                                                                      String line1,
                                                                      String line2,
                                                                      String city,
                                                                      CountryCode country,
                                                                      BigDecimal latitude,
                                                                      BigDecimal longitude,
                                                                      String createdBy) {
        final Address address = new Address(
                externalId,
                UUID.randomUUID().toString(),
                customerExternalId,
                addressName,
                line1,
                line2,
                city,
                country,
                latitude,
                longitude,
                null,
                createdBy,
                null,
                null,
                0L
        );
        ModelValidator.validate(address);

        return ResultWithEvents.of(address, Collections.singletonList(AddressAddedEvent.of(address)));
    }
}
