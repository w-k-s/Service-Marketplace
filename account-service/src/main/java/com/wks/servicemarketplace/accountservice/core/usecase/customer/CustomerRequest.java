package com.wks.servicemarketplace.accountservice.core.usecase.customer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Value
@JsonDeserialize(builder = CustomerRequest.Builder.class)
@Builder(builderClassName = "Builder", toBuilder = true)
public class CustomerRequest {

    @NonNull
    @NotBlank
    @Size(min = 2, max = 50)
    private String firstName;

    @NonNull
    @NotBlank
    @Size(min = 2, max = 50)
    private String lastName;

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder { }
}