package com.wks.servicemarketplace.accountservice.adapters.web.resources;

import com.coxautodev.graphql.tools.GraphQLRootResolver;
import com.wks.servicemarketplace.accountservice.core.usecase.address.AddressResponse;
import com.wks.servicemarketplace.accountservice.core.usecase.address.FindAddressByCustomerUuidUseCase;

import java.util.List;

public class Query implements GraphQLRootResolver {

    private final FindAddressByCustomerUuidUseCase findAddressByCustomerUuidUseCase;

    public Query(FindAddressByCustomerUuidUseCase findAddressByCustomerUuidUseCase) {
        this.findAddressByCustomerUuidUseCase = findAddressByCustomerUuidUseCase;
    }

    public List<AddressResponse> address(String customerUuid) throws Exception {
        return findAddressByCustomerUuidUseCase.execute(customerUuid);
    }
}
