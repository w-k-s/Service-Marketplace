package com.wks.servicemarketplace.customerservice.api;

import com.wks.servicemarketplace.common.AddressId;
import com.wks.servicemarketplace.common.CustomerId;
import com.wks.servicemarketplace.common.events.DomainEvent;
import com.wks.servicemarketplace.common.events.EventType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Value
@AllArgsConstructor
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

    @Override
    public EventType getEventType() {
        return EventType.ADDRESS_ADDED;
    }

    @Override
    public String getEntityType() {
        return "Address";
    }
}
