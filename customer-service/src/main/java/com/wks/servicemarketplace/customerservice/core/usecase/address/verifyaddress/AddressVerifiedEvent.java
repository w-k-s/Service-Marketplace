package com.wks.servicemarketplace.customerservice.core.usecase.address.verifyaddress;

import lombok.NonNull;
import lombok.Value;

@Value
public class AddressVerifiedEvent {
    @NonNull
    private final String orderId;
    @NonNull
    private final Long customerExternalId;
    @NonNull
    private final Long addressExternalId;
}
