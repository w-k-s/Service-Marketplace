package com.wks.servicemarketplace.accountservice.adapters.web.resources;

import com.wks.servicemarketplace.accountservice.core.usecase.UseCaseException;
import com.wks.servicemarketplace.accountservice.core.usecase.address.AddAddressUseCase;
import com.wks.servicemarketplace.accountservice.core.usecase.address.AddressRequest;
import com.wks.servicemarketplace.accountservice.core.usecase.address.AddressResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("addresses/v1/")
public class AddressResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddressResource.class);
    private final AddAddressUseCase addAddressUseCase;

    @Inject
    public AddressResource(AddAddressUseCase addAddressUseCase) {
        this.addAddressUseCase = addAddressUseCase;
    }

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public AddressResponse createAddress(AddressRequest addressRequest) throws UseCaseException {
        LOGGER.info("Add Address Request: '{}'", addressRequest);
        AddressResponse addressResponse = addAddressUseCase.execute(addressRequest);
        LOGGER.info("Add Address Response: '{}'", addressResponse);
        return addressResponse;
    }

}