package com.wks.servicemarketplace.accountservice.adapters.graphql;

import com.wks.servicemarketplace.accountservice.core.usecase.UseCaseException;
import com.wks.servicemarketplace.accountservice.core.usecase.customer.CreateCustomerUseCase;
import com.wks.servicemarketplace.accountservice.core.usecase.customer.CustomerRequest;
import com.wks.servicemarketplace.accountservice.core.usecase.customer.CustomerResponse;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CreateCustomerDataFetcher implements DataFetcher<CustomerResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateCustomerDataFetcher.class);

    private final CreateCustomerUseCase useCase;

    @Inject
    public CreateCustomerDataFetcher(CreateCustomerUseCase useCase) {
        LOGGER.info("Creating instance of CreateCustomerDataFetcher");
        this.useCase = useCase;
    }

    @Override
    public CustomerResponse get(DataFetchingEnvironment environment) {
        try {
            return useCase.execute(CustomerRequest.builder()
                    .firstName(environment.getArgument("firstName"))
                    .lastName(environment.getArgument("lastName"))
                    .user(environment.getContext())
                    .build());
        } catch (UseCaseException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
