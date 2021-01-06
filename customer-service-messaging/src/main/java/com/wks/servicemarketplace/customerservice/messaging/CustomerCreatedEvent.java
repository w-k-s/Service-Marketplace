package com.wks.servicemarketplace.customerservice.messaging;

import com.wks.servicemarketplace.common.CustomerId;
import com.wks.servicemarketplace.common.CustomerUUID;
import com.wks.servicemarketplace.common.Name;
import com.wks.servicemarketplace.common.events.DomainEvent;
import com.wks.servicemarketplace.common.events.EventType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
@AllArgsConstructor
public class CustomerCreatedEvent implements DomainEvent {

    @NonNull
    private CustomerId externalId;

    @NonNull
    private CustomerUUID uuid;

    @NonNull
    private Name name;

    @NonNull
    private String createdBy;

    private long version;

    @NotNull
    @Override
    public EventType getEventType() {
        return EventType.CUSTOMER_PROFILE_CREATED;
    }

    @NotNull
    @Override
    public String getEntityType() {
        return "Customer";
    }
}
