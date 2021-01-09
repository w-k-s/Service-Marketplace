package com.wks.servicemarketplace.customerservice.messaging;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.wks.servicemarketplace.common.CustomerId;
import com.wks.servicemarketplace.common.CustomerUUID;
import com.wks.servicemarketplace.common.Name;
import com.wks.servicemarketplace.common.events.DomainEvent;
import com.wks.servicemarketplace.common.events.EventType;

public class CustomerCreatedEvent implements DomainEvent {

    private final CustomerUUID uuid;
    private final Name name;
    private final String createdBy;
    private final long version;

    @JsonCreator
    public CustomerCreatedEvent(@JsonProperty("uuid") CustomerUUID uuid,
                                @JsonProperty("name") Name name,
                                @JsonProperty("createdBy") String createdBy,
                                @JsonProperty("version") long version) {
        this.uuid = uuid;
        this.name = name;
        this.createdBy = createdBy;
        this.version = version;
    }

    public CustomerUUID getUuid() {
        return uuid;
    }

    public Name getName() {
        return name;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public long getVersion() {
        return version;
    }

    @Override
    public final EventType getEventType() {
        return EventType.CUSTOMER_PROFILE_CREATED;
    }

    @Override
    public final String getEntityType() {
        return "Customer";
    }

}
