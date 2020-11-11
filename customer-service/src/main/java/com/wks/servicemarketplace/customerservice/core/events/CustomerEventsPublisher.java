package com.wks.servicemarketplace.customerservice.core.events;

import com.wks.servicemarketplace.customerservice.core.usecase.address.AddressAddedEvent;
import com.wks.servicemarketplace.customerservice.core.usecase.customer.CustomerCreatedEvent;
import com.wks.servicemarketplace.customerservice.core.usecase.address.verifyaddress.AddressVerificationFailedEvent;
import com.wks.servicemarketplace.customerservice.core.usecase.address.verifyaddress.AddressVerifiedEvent;

import java.io.IOException;
import java.util.List;

public interface CustomerEventsPublisher {
    void customerCreated(List<CustomerCreatedEvent> events) throws IOException;
    void addressAdded(List<AddressAddedEvent> events) throws IOException;
    void addressVerified(AddressVerifiedEvent event) throws IOException;
    void addressVerificationFailed(AddressVerificationFailedEvent event) throws IOException;
}
