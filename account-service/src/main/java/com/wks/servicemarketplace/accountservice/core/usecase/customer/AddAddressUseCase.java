package com.wks.servicemarketplace.accountservice.core.usecase.customer;

import com.wks.servicemarketplace.accountservice.core.daos.CustomerDao;
import com.wks.servicemarketplace.accountservice.core.daos.TransactionUtils;
import com.wks.servicemarketplace.accountservice.core.events.CustomerEventsPublisher;
import com.wks.servicemarketplace.accountservice.core.models.Address;
import com.wks.servicemarketplace.accountservice.core.models.CountryCode;
import com.wks.servicemarketplace.accountservice.core.models.ResultWithEvents;
import com.wks.servicemarketplace.accountservice.core.models.events.AddressAddedEvent;
import com.wks.servicemarketplace.accountservice.core.usecase.UseCase;
import com.wks.servicemarketplace.accountservice.core.usecase.UseCaseException;
import com.wks.servicemarketplace.accountservice.core.usecase.errors.ErrorType;
import com.wks.servicemarketplace.accountservice.core.utils.CloseableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.sql.Connection;

public class AddAddressUseCase implements UseCase<AddressRequest, AddressResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateCustomerUseCase.class);

    private final CustomerDao customerDao;
    private final CustomerEventsPublisher customerEventsPublisher;

    @Inject
    public AddAddressUseCase(CustomerDao customerDao, CustomerEventsPublisher customerEventsPublisher) {
        this.customerDao = customerDao;
        this.customerEventsPublisher = customerEventsPublisher;
    }

    @Override
    public AddressResponse execute(AddressRequest request) throws UseCaseException {
        Connection connection = null;
        try {
            connection = TransactionUtils.beginTransaction(customerDao.getConnection());
            final ResultWithEvents<Address, AddressAddedEvent> addressWithEvents = Address.create(
                    customerDao.newAddressExternalId(connection),
                    request.getCustomerExternalId(), // should be from token
                    request.getName(),
                    request.getLine1(),
                    request.getLine2(),
                    request.getCity(),
                    new CountryCode(request.getCountry()),
                    request.getLatitude(),
                    request.getLongitude(),
                    "John Doe" // from token
            );
            final Address address = addressWithEvents.getResult();

            customerDao.saveAddress(connection, address);
            connection.commit();

            customerEventsPublisher.addressAdded(
                    addressWithEvents.getEvents()
            );

            return AddressResponse.builder()
                    .externalId(address.getExternalId())
                    .customerExternalId(address.getCustomerExternalId())
                    .name(address.getName())
                    .line1(address.getLine1())
                    .line2(address.getLine2())
                    .city(address.getCity())
                    .country(address.getCountry().getCountryCode())
                    .latitude(address.getLatitude())
                    .longitude(address.getLongitude())
                    .build();
        } catch (UseCaseException e){
            LOGGER.error("Failed to add address.", e);
            TransactionUtils.rollback(connection);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to add address.", e);
            TransactionUtils.rollback(connection);
            throw new UseCaseException(ErrorType.ADDRESS_NOT_CREATED, e);
        } finally {
            CloseableUtils.close(connection);
        }
    }
}
