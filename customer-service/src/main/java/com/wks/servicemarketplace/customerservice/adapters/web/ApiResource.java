package com.wks.servicemarketplace.customerservice.adapters.web;

import com.wks.servicemarketplace.common.CustomerUUID;
import com.wks.servicemarketplace.common.auth.Authentication;
import com.wks.servicemarketplace.common.auth.User;
import com.wks.servicemarketplace.common.errors.CoreException;
import com.wks.servicemarketplace.common.errors.UserNotFoundException;
import com.wks.servicemarketplace.customerservice.api.AddressRequest;
import com.wks.servicemarketplace.customerservice.api.CustomerService;
import com.wks.servicemarketplace.customerservice.core.usecase.address.AddAddressUseCase;
import com.wks.servicemarketplace.customerservice.core.usecase.address.FindAddressByCustomerUuidUseCase;
import org.glassfish.jersey.process.internal.RequestScoped;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.Optional;

@Path(CustomerService.PATH_V1_)
@RequestScoped
public class ApiResource {

    private final FindAddressByCustomerUuidUseCase findAddressUseCase;
    private final AddAddressUseCase addAddressUseCase;

    @Inject
    public ApiResource(FindAddressByCustomerUuidUseCase findAddressUseCase, AddAddressUseCase addAddressUseCase) {
        this.addAddressUseCase = addAddressUseCase;
        this.findAddressUseCase = findAddressUseCase;
    }

    @GET
    @Path(CustomerService.ENDPOINT_GET_ADDRESSES)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAddresses(@Context SecurityContext securityContext) throws UserNotFoundException {
        final var authentication = (Authentication) securityContext.getUserPrincipal();
        final var customerId = Optional.ofNullable(authentication.getUser())
                .map(User::getId)
                .map(CustomerUUID::of)
                .orElseThrow(UserNotFoundException::new);

        return Response.ok(findAddressUseCase.execute(customerId)).build();
    }

    @POST
    @Path(CustomerService.ENDPOINT_ADD_ADDRESS)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAddress(final AddressRequest.Builder addressRequest,
                               @Context SecurityContext securityContext) throws CoreException {
        final var authentication = (Authentication) securityContext.getUserPrincipal();
        return Response.ok(addAddressUseCase.execute(
                addressRequest
                        .authentication(authentication)
                        .build())
        ).build();
    }
}
