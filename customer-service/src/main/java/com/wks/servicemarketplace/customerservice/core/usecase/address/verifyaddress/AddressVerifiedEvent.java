package com.wks.servicemarketplace.customerservice.core.usecase.address.verifyaddress;

import com.wks.servicemarketplace.customerservice.core.usecase.address.AddressId;
import com.wks.servicemarketplace.customerservice.core.usecase.customer.CustomerId;
import lombok.NonNull;
import lombok.Value;

@Value
public class AddressVerifiedEvent {
    @NonNull
    private final String orderId;
    @NonNull
    private final CustomerId customerExternalId;
    @NonNull
    private final AddressId addressExternalId;
}
