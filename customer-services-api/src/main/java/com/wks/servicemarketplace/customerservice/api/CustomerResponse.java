package com.wks.servicemarketplace.customerservice.api;

import com.wks.servicemarketplace.common.CustomerId;
import com.wks.servicemarketplace.common.CustomerUUID;
import com.wks.servicemarketplace.common.Name;
import lombok.*;

import java.util.List;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CustomerResponse {
    @NonNull
    private CustomerUUID uuid;
    @NonNull
    private CustomerId externalId;
    @NonNull
    private Name name;
    @NonNull
    private List<AddressResponse> addresses;
    @NonNull
    private Long version;
}
