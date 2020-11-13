package com.wks.servicemarketplace.customerservice.core.usecase.address;

import com.wks.servicemarketplace.customerservice.core.usecase.customer.CustomerId;
import com.wks.servicemarketplace.customerservice.core.usecase.DomainEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AddressAddedEvent implements DomainEvent {

    private final AddressId externalId;

    @NonNull
    private final CustomerId customerExternalId;

    @NonNull
    private final String name;

    @NonNull
    private final String line1;

    private final String line2;

    @NotBlank
    private final String city;

    @NotNull
    private final String country;

    @NonNull
    private final BigDecimal latitude;

    @NonNull
    private final BigDecimal longitude;

    @NonNull
    private final String createdBy;

    @NonNull
    private final Long version;

    public static AddressAddedEvent of(Address address) {
        return new AddressAddedEvent(
                address.getExternalId(),
                address.getCustomerExternalId(),
                address.getName(),
                address.getLine1(),
                address.getLine2(),
                address.getCity(),
                address.getCountry().getCountryCode(),
                address.getLatitude(),
                address.getLongitude(),
                address.getCreatedBy(),
                address.getVersion()
        );
    }
}
