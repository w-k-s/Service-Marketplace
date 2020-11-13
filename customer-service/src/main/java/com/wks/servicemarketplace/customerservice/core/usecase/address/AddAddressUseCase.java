package com.wks.servicemarketplace.customerservice.core.usecase.address;

import com.wks.servicemarketplace.customerservice.core.auth.AuthorizationUtils;
import com.wks.servicemarketplace.customerservice.core.auth.User;
import com.wks.servicemarketplace.customerservice.core.daos.CustomerDao;
import com.wks.servicemarketplace.customerservice.core.daos.TransactionUtils;
import com.wks.servicemarketplace.customerservice.core.events.CustomerEventsPublisher;
import com.wks.servicemarketplace.customerservice.core.exceptions.AuthenticationRequiredException;
import com.wks.servicemarketplace.customerservice.core.exceptions.CoreException;
import com.wks.servicemarketplace.customerservice.core.exceptions.ErrorType;
import com.wks.servicemarketplace.customerservice.core.exceptions.UserNotFoundException;
import com.wks.servicemarketplace.customerservice.core.usecase.ResultWithEvents;
import com.wks.servicemarketplace.customerservice.core.usecase.UseCase;
import com.wks.servicemarketplace.customerservice.core.usecase.customer.CreateCustomerUseCase;
import com.wks.servicemarketplace.customerservice.core.usecase.customer.Customer;
import com.wks.servicemarketplace.customerservice.core.utils.CloseableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.sql.Connection;

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
                    request.getAuthentication()
                            .getUser()
                            .map(User::getId)
                            .orElseThrow(AuthenticationRequiredException::new)
            ).orElseThrow(UserNotFoundException::new);

            final Address address = Address.create(
                    customerDao.newAddressExternalId(connection),
                    customer.getExternalId(),
                    request.getName(),
                    request.getLine1(),
                    request.getLine2(),
                    request.getCity(),
                    new CountryCode(request.getCountry()),
                    request.getLatitude(),
                    request.getLongitude(),
                    request.getAuthentication().getName()
            );

            final ResultWithEvents<Customer, AddressAddedEvent> customerWithEvents = customer.addAddress(address, request.getAuthentication().getName());

            customerDao.saveAddress(connection, address);
            connection.commit();

            customerEventsPublisher.addressAdded(customerWithEvents.getEvents());

            return AddressResponse.builder()
                    .uuid(address.getUuid())
                    .externalId(address.getExternalId())
                    .customerExternalId(address.getCustomerExternalId())
                    .name(address.getName())
                    .line1(address.getLine1())
                    .line2(address.getLine2())
                    .city(address.getCity())
                    .country(address.getCountry().getCountryCode())
                    .latitude(address.getLatitude())
                    .longitude(address.getLongitude())
                    .version(address.getVersion())
                    .build();
        } catch (CoreException e) {
            LOGGER.error("Failed to add address.", e);
            TransactionUtils.rollback(connection);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to add address.", e);
            TransactionUtils.rollback(connection);
            throw new CoreException(ErrorType.ADDRESS_NOT_CREATED, e);
        } finally {
            CloseableUtils.close(connection);
        }
    }
}
