package com.wks.servicemarketplace.customerservice.api;

import com.wks.servicemarketplace.common.errors.CoreException;
import com.wks.servicemarketplace.common.events.DefaultFailureEvent;
import com.wks.servicemarketplace.common.events.EventType;

public class CustomerCreationFailedEvent extends DefaultFailureEvent {

    public CustomerCreationFailedEvent(CoreException exception) {
        super(EventType.CUSTOMER_PROFILE_CREATION_FAILED,
                "Customer",
                exception.getErrorType(),
                exception.getMessage()
        );
    }
}
