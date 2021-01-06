package com.wks.servicemarketplace.customerservice.core.usecase.customer;

import com.wks.servicemarketplace.common.Name;
import com.wks.servicemarketplace.common.errors.CoreException;
import com.wks.servicemarketplace.common.errors.ErrorType;
import com.wks.servicemarketplace.customerservice.api.CustomerRequest;
import com.wks.servicemarketplace.customerservice.api.CustomerResponse;
import com.wks.servicemarketplace.customerservice.core.auth.AuthorizationUtils;
import com.wks.servicemarketplace.customerservice.core.daos.CustomerDao;
import com.wks.servicemarketplace.customerservice.core.daos.TransactionUtils;
import com.wks.servicemarketplace.customerservice.core.events.CustomerEventsPublisher;
import com.wks.servicemarketplace.customerservice.core.usecase.ResultWithEvents;
import com.wks.servicemarketplace.customerservice.core.usecase.UseCase;
import com.wks.servicemarketplace.customerservice.core.utils.CloseableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.sql.Connection;
import java.util.Collections;

public class CreateCustomerUseCase implements UseCase<CustomerRequest, CustomerResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateCustomerUseCase.class);

    private final CustomerDao customerDao;
    private final CustomerEventsPublisher customerEventsPublisher;

    @Inject
    public CreateCustomerUseCase(CustomerDao customerDao,
                                 CustomerEventsPublisher customerEventsPublisher) {
        this.customerDao = customerDao;
        this.customerEventsPublisher = customerEventsPublisher;
    }

    @Override
    public CustomerResponse execute(CustomerRequest customerRequest) throws CoreException {
        Connection connection = null;
        try {
            AuthorizationUtils.checkRole(customerRequest.getAuthentication(), "account.create");

            connection = TransactionUtils.beginTransaction(customerDao.getConnection());

            ResultWithEvents<Customer, CustomerCreatedEvent> customerAndEvents = Customer.create(
                    customerDao.newCustomerExternalId(connection),
                    Name.of(customerRequest.getFirstName(), customerRequest.getLastName()),
                    customerRequest.getEmail()
            );
            final Customer customer = customerAndEvents.getResult();

            customerDao.saveCustomer(connection, customer);
            connection.commit();

            customerEventsPublisher.customerCreated(customerAndEvents.getEvents());

            return CustomerResponse
                    .builder()
                    .uuid(customer.getUuid())
                    .externalId(customer.getExternalId())
                    .firstName(customer.getName().getFirstName())
                    .lastName(customer.getName().getLastName())
                    .addresses(Collections.emptyList())
                    .version(customer.getVersion())
                    .build();
        } catch (Exception e) {
            LOGGER.error("Failed to create customer.", e);
            TransactionUtils.rollback(connection);
            throw new CoreException(ErrorType.UNKNOWN, "Failed to create customer profile", e);
        } finally {
            CloseableUtils.close(connection);
        }
    }
}
