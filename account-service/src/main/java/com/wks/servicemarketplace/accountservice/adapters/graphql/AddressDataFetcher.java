package com.wks.servicemarketplace.accountservice.adapters.graphql;

import com.wks.servicemarketplace.accountservice.core.usecase.address.AddressResponse;
import com.wks.servicemarketplace.accountservice.core.usecase.address.FindAddressByCustomerUuidUseCase;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;

@Singleton
public class AddressDataFetcher implements DataFetcher<List<AddressResponse>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddressDataFetcher.class);

    private final FindAddressByCustomerUuidUseCase useCase;

    @Inject
    public AddressDataFetcher(FindAddressByCustomerUuidUseCase useCase) {
        LOGGER.info("Creating instance of AddressDataFetcher");
        this.useCase = useCase;
    }

    @Override
    public List<AddressResponse> get(DataFetchingEnvironment environment) {
        try {
            return useCase.execute(environment.getArgument("customerUuid"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
