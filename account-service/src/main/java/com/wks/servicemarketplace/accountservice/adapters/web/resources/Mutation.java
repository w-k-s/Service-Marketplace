package com.wks.servicemarketplace.accountservice.adapters.web.resources;

import com.coxautodev.graphql.tools.GraphQLRootResolver;
import com.wks.servicemarketplace.accountservice.core.usecase.UseCaseException;
import com.wks.servicemarketplace.accountservice.core.usecase.address.AddAddressUseCase;
import com.wks.servicemarketplace.accountservice.core.usecase.address.AddressRequest;
import com.wks.servicemarketplace.accountservice.core.usecase.address.AddressResponse;
import com.wks.servicemarketplace.accountservice.core.usecase.customer.CreateCustomerUseCase;
import com.wks.servicemarketplace.accountservice.core.usecase.customer.CustomerRequest;
import com.wks.servicemarketplace.accountservice.core.usecase.customer.CustomerResponse;
import graphql.ExecutionResult;

import java.math.BigDecimal;

public class Mutation implements GraphQLRootResolver {

    private final CreateCustomerUseCase createCustomerUseCase;
    private final AddAddressUseCase addressUseCase;

    public Mutation(CreateCustomerUseCase createCustomerUseCase, AddAddressUseCase addressUseCase) {
        this.createCustomerUseCase = createCustomerUseCase;
        this.addressUseCase = addressUseCase;
    }

    public CustomerResponse createCustomer(String firstName, String lastName) throws UseCaseException {
        return createCustomerUseCase.execute(CustomerRequest.builder()
                .firstName(firstName)
                .lastName(lastName)
                .build());
    }

    public AddressResponse createAddress(
            Long customerExternalId,
            String name,
            String line1,
            String line2,
            String city,
            String country,
            BigDecimal latitude,
            BigDecimal longitude) throws UseCaseException {
        return addressUseCase.execute(AddressRequest.builder()
                .customerExternalId(customerExternalId)
                .name(name)
                .line1(line1)
                .line2(line2)
                .city(city)
                .country(country)
                .latitude(latitude)
                .longitude(longitude)
                .build());
    }

    /// TODO: Types
}
