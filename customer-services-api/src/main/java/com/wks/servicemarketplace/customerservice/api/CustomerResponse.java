package com.wks.servicemarketplace.customerservice.api;

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
    private String firstName;
    @NonNull
    private String lastName;
    @NonNull
    private List<AddressResponse> addresses;
    @NonNull
    private Long version;
}
