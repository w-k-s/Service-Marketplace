package com.wks.servicemarketplace.customerservice.core.usecase.address;

import com.google.common.base.Preconditions;
import com.wks.servicemarketplace.common.*;
import com.wks.servicemarketplace.customerservice.core.usecase.ResultWithEvents;
import com.wks.servicemarketplace.customerservice.core.usecase.customer.Customer;
import com.wks.servicemarketplace.customerservice.messaging.AddressAddedEvent;
import lombok.NonNull;
import lombok.Value;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.Collections;

@Value
public class Address {

    private static final int MAX_ADDRESSES = 5;

    @NonNull
    private final AddressId externalId;

    @NonNull
    private final AddressUUID uuid;

    @NonNull
    @NotNull
    private final CustomerId customerExternalId;

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

    private final OffsetDateTime createdDate;

    private final String createdBy;

    private final OffsetDateTime lastModifiedDate;

    private final String lastModifiedBy;

    @NonNull
    private final Long version;

    public BigDecimal getLatitude() {
        return this.latitude.setScale(5, RoundingMode.HALF_UP);
    }

    public BigDecimal getLongitude() {
        return this.longitude.setScale(5, RoundingMode.HALF_UP);
    }

    static ResultWithEvents<Address, AddressAddedEvent> create(Customer customer,
                                                               AddressId externalId,
                                                               String addressName,
                                                               String line1,
                                                               String line2,
                                                               String city,
                                                               CountryCode country,
                                                               BigDecimal latitude,
                                                               BigDecimal longitude,
                                                               String createdBy) {
        Preconditions.checkNotNull(customer);
        Preconditions.checkArgument(customer.getAddresses().size() < MAX_ADDRESSES, "Customer can not have more than 5 addresses");

        final Address address = new Address(
                externalId,
                AddressUUID.random(),
                customer.getExternalId(),
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

        return ResultWithEvents.of(ModelValidator.validate(address), Collections.singletonList(new AddressAddedEvent(
                address.getExternalId(),
                address.getCustomerExternalId(),
                address.getName(),
                address.getLine1(),
                address.getLine2(),
                address.getCity(),
                address.getCountry().toString(),
                address.getLatitude(),
                address.getLongitude(),
                address.getCreatedBy(),
                address.getVersion()
        )));
    }
}
