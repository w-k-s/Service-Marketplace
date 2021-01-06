package com.wks.servicemarketplace.customerservice.core.usecase.address;

import com.wks.servicemarketplace.common.CountryCode;
import com.wks.servicemarketplace.common.UserId;
import com.wks.servicemarketplace.common.auth.Authentication;
import com.wks.servicemarketplace.common.auth.User;
import com.wks.servicemarketplace.common.errors.CoreException;
import com.wks.servicemarketplace.common.errors.UserNotFoundException;
import com.wks.servicemarketplace.customerservice.api.AddressRequest;
import com.wks.servicemarketplace.customerservice.api.AddressResponse;
import com.wks.servicemarketplace.customerservice.api.CustomerUUID;
import com.wks.servicemarketplace.customerservice.core.auth.AuthorizationUtils;
import com.wks.servicemarketplace.customerservice.core.daos.CustomerDao;
import com.wks.servicemarketplace.customerservice.core.daos.TransactionUtils;
import com.wks.servicemarketplace.customerservice.core.events.CustomerEventsPublisher;
import com.wks.servicemarketplace.customerservice.core.usecase.ResultWithEvents;
import com.wks.servicemarketplace.customerservice.core.usecase.UseCase;
import com.wks.servicemarketplace.customerservice.core.usecase.customer.CreateCustomerUseCase;
import com.wks.servicemarketplace.customerservice.core.usecase.customer.Customer;
import com.wks.servicemarketplace.customerservice.core.utils.CloseableUtils;
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
    private final CustomerEventsPublisher customerEventsPublisher;

    @Inject
    public AddAddressUseCase(CustomerDao customerDao,
                             CustomerEventsPublisher customerEventsPublisher) {
        this.customerDao = customerDao;
        this.customerEventsPublisher = customerEventsPublisher;
    }

    @Override
    public AddressResponse execute(AddressRequest request) throws CoreException {
        Connection connection = null;
        try {
            AuthorizationUtils.checkRole(request.getAuthentication(), "address.create");

            connection = TransactionUtils.beginTransaction(customerDao.getConnection());

            final Customer customer = customerDao.findCustomerByUuid(
                    connection,
                    Optional.ofNullable(request.getAuthentication())
                            .map(Authentication::getUser)
                            .map(User::getId)
                            .map(UserId::getValue)
                            .map(CustomerUUID::of)
                            .orElseThrow(UserNotFoundException::new)
            ).orElseThrow(UserNotFoundException::new);

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
            connection.commit();

            customerEventsPublisher.addressAdded(addressAndEvents.getEvents());

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
