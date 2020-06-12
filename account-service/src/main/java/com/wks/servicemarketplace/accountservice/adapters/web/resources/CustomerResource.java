package com.wks.servicemarketplace.accountservice.adapters.web.resources;

import com.wks.servicemarketplace.accountservice.core.usecase.UseCaseException;
import com.wks.servicemarketplace.accountservice.core.usecase.customer.CreateCustomerUseCase;
import com.wks.servicemarketplace.accountservice.core.usecase.customer.CustomerRequest;
import com.wks.servicemarketplace.accountservice.core.usecase.customer.CustomerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("customers/v1/")
public class CustomerResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerResource.class);
    private final CreateCustomerUseCase createCustomerUseCase;

    @Inject
    public CustomerResource(CreateCustomerUseCase createCustomerUseCase) {
        this.createCustomerUseCase = createCustomerUseCase;
    }

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CustomerResponse createCustomer(CustomerRequest customerRequest) throws UseCaseException {
        LOGGER.info("Create Customer Request: '{}'", customerRequest);
        CustomerResponse customerResponse = createCustomerUseCase.execute(customerRequest);
        LOGGER.info("Create Customer Response: '{}'", customerResponse);
        return customerResponse;
    }

}