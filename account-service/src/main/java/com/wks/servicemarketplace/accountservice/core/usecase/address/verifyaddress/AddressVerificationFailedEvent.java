package com.wks.servicemarketplace.accountservice.core.usecase.address.verifyaddress;

import lombok.NonNull;
import lombok.Value;

import java.util.Map;

@Value
public class AddressVerificationFailedEvent {
    @NonNull
    private final String orderId;
    @NonNull
    private final Integer code;
    @NonNull
    private final String type;
    private final String description;
    @NonNull
    private final Map<String,String> userInfo;
}
