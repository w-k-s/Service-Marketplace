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

    public AddressResponse createAddress(AddressRequest addressRequest) throws UseCaseException {
        return addressUseCase.execute(addressRequest);
    }

    /// TODO: Types
}
