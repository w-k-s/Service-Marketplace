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
    private Long externalId;

    @NonNull
    private String uuid;

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
                customer.getFirstName(),
                customer.getLastName(),
                customer.getCreatedBy(),
                customer.getVersion()
        );
    }
}
