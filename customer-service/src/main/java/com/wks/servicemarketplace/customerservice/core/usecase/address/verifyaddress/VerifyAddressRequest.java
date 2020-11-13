package com.wks.servicemarketplace.customerservice.core.usecase.address.verifyaddress;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.wks.servicemarketplace.customerservice.core.usecase.address.AddressId;
import com.wks.servicemarketplace.customerservice.core.usecase.customer.CustomerId;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@JsonDeserialize(builder = VerifyAddressRequest.Builder.class)
@Builder(builderClassName = "Builder", toBuilder = true)
public class VerifyAddressRequest {
    private String orderId;
    private CustomerId customerExternalId;
    private AddressId addressExternalId;
    private BigDecimal addressLatitude;
    private BigDecimal addressLongitude;
    private Long addressVersion;

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
    }
}
