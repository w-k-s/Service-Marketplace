package com.wks.servicemarketplace.customerservice.messaging;

import com.wks.servicemarketplace.common.errors.CoreThrowable;
import com.wks.servicemarketplace.common.events.DefaultFailureEvent;
import com.wks.servicemarketplace.common.events.EventType;

public class CustomerCreationFailedEvent extends DefaultFailureEvent {

    public CustomerCreationFailedEvent(CoreThrowable exception) {
        super(EventType.CUSTOMER_PROFILE_CREATION_FAILED, "Customer", exception);
    }
}
