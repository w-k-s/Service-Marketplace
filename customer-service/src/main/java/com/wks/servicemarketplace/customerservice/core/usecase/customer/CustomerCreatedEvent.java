package com.wks.servicemarketplace.customerservice.core.usecase.customer;

import com.wks.servicemarketplace.customerservice.core.usecase.DomainEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

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
                customer.getName().firstName,
                customer.getName().lastName,
                customer.getCreatedBy(),
                customer.getVersion()
        );
    }
}
