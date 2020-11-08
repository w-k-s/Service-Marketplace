package com.wks.servicemarketplace.customerservice.core.usecase.address;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class AddressResponse {
    @NonNull
    private final String uuid;
    @NonNull
    private final Long externalId;
    @NonNull
    private final Long customerExternalId;
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
