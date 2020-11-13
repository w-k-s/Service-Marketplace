package com.wks.servicemarketplace.customerservice.core.usecase.address;

import com.wks.servicemarketplace.customerservice.core.usecase.customer.CustomerId;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class AddressResponse {
    @NonNull
    private final AddressUUID uuid;
    @NonNull
    private final AddressId externalId;
    @NonNull
    private final CustomerId customerExternalId;
    @NonNull
    private final String name;
    @NonNull
    private final String line1;
    private final String line2;
    @NonNull
    private final String city;
    @NotNull
    private final String country;
    @NonNull
    private final BigDecimal latitude;
    @NonNull
    private final BigDecimal longitude;
    @NonNull
    private final Long version;
}
