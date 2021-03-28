package com.wks.servicemarketplace.customerservice.core.usecase.address;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wks.servicemarketplace.common.CountryCode;
import com.wks.servicemarketplace.common.CustomerUUID;
import com.wks.servicemarketplace.common.auth.Authentication;
import com.wks.servicemarketplace.common.auth.Permission;
import com.wks.servicemarketplace.common.errors.CoreException;
import com.wks.servicemarketplace.common.errors.ErrorType;
import com.wks.servicemarketplace.common.events.EventEnvelope;
import com.wks.servicemarketplace.customerservice.api.AddressRequest;
import com.wks.servicemarketplace.customerservice.api.AddressResponse;
import com.wks.servicemarketplace.customerservice.core.auth.AuthorizationUtils;
import com.wks.servicemarketplace.customerservice.core.daos.CustomerDao;
import com.wks.servicemarketplace.customerservice.core.daos.EventDao;
import com.wks.servicemarketplace.customerservice.core.daos.TransactionUtils;
import com.wks.servicemarketplace.customerservice.core.usecase.ResultWithEvents;
import com.wks.servicemarketplace.customerservice.core.usecase.UseCase;
import com.wks.servicemarketplace.customerservice.core.usecase.customer.CreateCustomerUseCase;
import com.wks.servicemarketplace.customerservice.core.usecase.customer.Customer;
import com.wks.servicemarketplace.customerservice.core.utils.CloseableUtils;
import com.wks.servicemarketplace.customerservice.api.AddressAddedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class AddAddressUseCase implements UseCase<AddressRequest, AddressResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateCustomerUseCase.class);

    private final CustomerDao customerDao;
    private final EventDao eventDao;
    private final ObjectMapper objectMapper;

    @Inject
    public AddAddressUseCase(CustomerDao customerDao,
                             EventDao eventDao,
                             ObjectMapper objectMapper) {
        this.customerDao = customerDao;
        this.eventDao = eventDao;
        this.objectMapper = objectMapper;
    }

    @Override
    public AddressResponse execute(AddressRequest request) throws CoreException {
        Connection connection = null;
        try {
            AuthorizationUtils.checkRole(request.getAuthentication(), Permission.CREATE_ADDRESS);

            connection = TransactionUtils.beginTransaction(customerDao.getConnection());

            final Customer customer = customerDao.findCustomerByUuid(
                    connection,
                    Optional.ofNullable(request.getAuthentication())
                            .map(Authentication::getUserId)
                            .map(CustomerUUID::of)
                            .orElseThrow(() -> new CoreException(ErrorType.AUTHENTICATION, "token does not contain user id", null, null))
            ).orElseThrow(() -> new CoreException(ErrorType.RESOURCE_NOT_FOUND, "User not found", null, null));

            final ResultWithEvents<Address, AddressAddedEvent> addressAndEvents = Address.create(
                    customer,
                    customerDao.newAddressExternalId(connection),
                    request.getName(),
                    request.getLine1(),
                    request.getLine2(),
                    request.getCity(),
                    CountryCode.of(request.getCountry()),
                    request.getLatitude(),
                    request.getLongitude(),
                    request.getAuthentication().getName()
            );

            final Address address = addressAndEvents.getResult();
            customerDao.saveAddress(connection, address);
            eventDao.saveEvent(connection, new EventEnvelope(
                    addressAndEvents.firstEvent(),
                    address.getUuid().toString(),
                    objectMapper.writeValueAsString(addressAndEvents.firstEvent())
            ));
            connection.commit();

            return AddressResponse.builder()
                    .uuid(address.getUuid())
                    .externalId(address.getExternalId())
                    .customerExternalId(address.getCustomerExternalId())
                    .name(address.getName())
                    .line1(address.getLine1())
                    .line2(address.getLine2())
                    .city(address.getCity())
                    .country(address.getCountry().toString())
                    .latitude(address.getLatitude())
                    .longitude(address.getLongitude())
                    .version(address.getVersion())
                    .build();
        } catch (CoreException e) {
            LOGGER.error("Failed to add address.", e);
            TransactionUtils.rollback(connection);
            throw e;
        } catch (SQLException | IOException e) {
            LOGGER.error("Failed to add address.", e);
            TransactionUtils.rollback(connection);
            throw new RuntimeException("Failed to add address", e);
        } finally {
            CloseableUtils.close(connection);
        }
    }
}
