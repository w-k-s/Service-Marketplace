package com.wks.servicemarketplace.accountservice.core.events;

import com.wks.servicemarketplace.accountservice.core.models.events.AddressAddedEvent;
import com.wks.servicemarketplace.accountservice.core.models.events.CustomerCreatedEvent;

import java.io.IOException;
import java.util.List;

public interface CustomerEventsPublisher {
    void customerCreated(List<CustomerCreatedEvent> events) throws IOException;
    void addressAdded(List<AddressAddedEvent> events) throws IOException;
}
