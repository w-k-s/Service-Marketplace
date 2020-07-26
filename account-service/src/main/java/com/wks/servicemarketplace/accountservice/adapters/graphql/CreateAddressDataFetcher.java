package com.wks.servicemarketplace.accountservice.adapters.graphql;

import com.wks.servicemarketplace.accountservice.core.usecase.address.AddAddressUseCase;
import com.wks.servicemarketplace.accountservice.core.usecase.address.AddressRequest;
import com.wks.servicemarketplace.accountservice.core.usecase.address.AddressResponse;
import com.wks.servicemarketplace.accountservice.core.utils.Dictionary;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.Map;

@Singleton
public class CreateAddressDataFetcher implements DataFetcher<AddressResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateAddressDataFetcher.class);

    private final AddAddressUseCase useCase;

    @Inject
    public CreateAddressDataFetcher(AddAddressUseCase useCase) {
        LOGGER.info("Creating instance of CreateAddressDataFetcher");
        this.useCase = useCase;
    }

    @Override
    public AddressResponse get(DataFetchingEnvironment environment) {
        try {
            final Dictionary<String> address = Dictionary.of(environment.getArgument("address"));

            return useCase.execute(AddressRequest.builder()
                    .customerExternalId(address.get("customerExternalId"))
                    .name(address.get("name"))
                    .line1(address.get("line1"))
                    .line2(address.get("line2"))
                    .city(address.get("city"))
                    .country(address.get("country"))
                    .latitude(new BigDecimal(address.get("latitude").toString()))
                    .longitude(new BigDecimal(address.get("longitude").toString()))
                    .user(environment.getContext())
                    .build());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
