package com.wks.servicemarketplace.customerservice.adapters.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wks.servicemarketplace.customerservice.core.usecase.address.AddAddressUseCase;
import com.wks.servicemarketplace.customerservice.core.usecase.address.AddressRequest;
import com.wks.servicemarketplace.customerservice.core.usecase.address.AddressResponse;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CreateAddressDataFetcher implements DataFetcher<AddressResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateAddressDataFetcher.class);

    private final AddAddressUseCase useCase;
    private final ObjectMapper objectMapper;

    @Inject
    public CreateAddressDataFetcher(AddAddressUseCase useCase,
                                    ObjectMapper objectMapper) {
        LOGGER.info("Creating instance of CreateAddressDataFetcher");
        this.useCase = useCase;
        this.objectMapper = objectMapper;
    }

    @Override
    public AddressResponse get(DataFetchingEnvironment environment) {
        try {
            final AddressRequest address = GraphQLUtils.getArgument(environment, "address", AddressRequest.Builder.class, objectMapper)
                    .map(builder -> builder.authentication(environment.getContext()).build())
                    .orElseThrow(() -> new IllegalArgumentException("Failed to parse request"));
            return useCase.execute(address);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
