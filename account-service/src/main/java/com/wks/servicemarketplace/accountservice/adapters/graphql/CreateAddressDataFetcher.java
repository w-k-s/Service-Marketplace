package com.wks.servicemarketplace.accountservice.adapters.graphql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wks.servicemarketplace.accountservice.core.usecase.UseCaseException;
import com.wks.servicemarketplace.accountservice.core.usecase.address.AddAddressUseCase;
import com.wks.servicemarketplace.accountservice.core.usecase.address.AddressRequest;
import com.wks.servicemarketplace.accountservice.core.usecase.address.AddressResponse;
import com.wks.servicemarketplace.accountservice.core.usecase.customer.CreateCustomerUseCase;
import com.wks.servicemarketplace.accountservice.core.usecase.customer.CustomerRequest;
import com.wks.servicemarketplace.accountservice.core.usecase.customer.CustomerResponse;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CreateAddressDataFetcher implements DataFetcher<AddressResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateAddressDataFetcher.class);

    private final AddAddressUseCase useCase;
    private final DataFetchingEnvironmentMapper argumentMapper;

    @Inject
    public CreateAddressDataFetcher(AddAddressUseCase useCase, DataFetchingEnvironmentMapper argumentMapper) {
        LOGGER.info("Creating instance of CreateAddressDataFetcher");
        this.useCase = useCase;
        this.argumentMapper = argumentMapper;
    }

    @Override
    public AddressResponse get(DataFetchingEnvironment environment) {
        try {
            AddressRequest request = argumentMapper.getArgument(environment, "address", AddressRequest.class);

            return useCase.execute(AddressRequest.builder()
                    .customerExternalId(request.getCustomerExternalId())
                    .name(request.getName())
                    .line1(request.getLine1())
                    .line2(request.getLine2())
                    .city(request.getCity())
                    .country(request.getCountry())
                    .latitude(request.getLatitude())
                    .longitude(request.getLongitude())
                    .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
