package com.wks.servicemarketplace.customerservice.core.usecase.customer;

import com.wks.servicemarketplace.common.events.DomainEvent;
import com.wks.servicemarketplace.common.events.EventType;
import com.wks.servicemarketplace.customerservice.api.CustomerId;
import com.wks.servicemarketplace.customerservice.api.CustomerUUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomerCreatedEvent implements DomainEvent {

    @NonNull
    private CustomerId externalId;

    @NonNull
    private CustomerUUID uuid;

    @NonNull
    private String firstName;

    @NonNull
    private String lastName;

    @NonNull
    private String createdBy;

    private long version;

    public static CustomerCreatedEvent of(Customer customer) {
        return new CustomerCreatedEvent(
                customer.getExternalId(),
                customer.getUuid(),
                customer.getName().getFirstName(),
                customer.getName().getLastName(),
                customer.getCreatedBy(),
                customer.getVersion()
        );
    }

    @NotNull
    @Override
    public EventType getEventType() {
        return EventType.CUSTOMER_PROFILE_CREATED;
    }

    @NotNull
    @Override
    public String getEntityType() {
        return Customer.class.getSimpleName();
    }
}
