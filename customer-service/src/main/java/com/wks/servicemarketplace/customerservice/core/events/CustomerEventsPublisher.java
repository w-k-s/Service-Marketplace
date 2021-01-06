package com.wks.servicemarketplace.customerservice.core.events;

import com.wks.servicemarketplace.customerservice.core.usecase.address.AddressAddedEvent;
import com.wks.servicemarketplace.customerservice.core.usecase.customer.CustomerCreatedEvent;

import java.io.IOException;
import java.util.List;

public interface CustomerEventsPublisher {
    void customerCreated(List<CustomerCreatedEvent> events) throws IOException;
    void addressAdded(List<AddressAddedEvent> events) throws IOException;
}
