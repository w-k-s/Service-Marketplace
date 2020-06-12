package com.wks.servicemarketplace.accountservice.core.usecase.customer;

import com.wks.servicemarketplace.accountservice.core.daos.CustomerDao;
import com.wks.servicemarketplace.accountservice.core.daos.TransactionUtils;
import com.wks.servicemarketplace.accountservice.core.models.Customer;
import com.wks.servicemarketplace.accountservice.core.models.ResultWithEvents;
import com.wks.servicemarketplace.accountservice.core.models.events.CustomerCreatedEvent;
import com.wks.servicemarketplace.accountservice.core.usecase.UseCase;
import com.wks.servicemarketplace.accountservice.core.usecase.UseCaseException;
import com.wks.servicemarketplace.accountservice.core.usecase.errors.ErrorType;
import com.wks.servicemarketplace.accountservice.core.utils.CloseableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.sql.Connection;
import java.util.Collections;

public class CreateCustomerUseCase implements UseCase<CustomerRequest, CustomerResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateCustomerUseCase.class);

    private final CustomerDao customerDao;

    @Inject
    public CreateCustomerUseCase(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    @Override
    public CustomerResponse execute(CustomerRequest customerRequest) throws UseCaseException {

        Connection connection = null;
        try {
            connection = TransactionUtils.beginTransaction(customerDao.getConnection());

            ResultWithEvents<Customer, CustomerCreatedEvent> customerAndEvents = Customer.create(
                    customerDao.newCustomerExternalId(connection),
                    customerRequest.getFirstName(),
                    customerRequest.getLastName(),
                    "John Doe" // get from token
            );
            final Customer customer = customerAndEvents.getResult();

            customerDao.saveCustomer(connection, customer);

            // TODO: Save events;
            connection.commit();

            return CustomerResponse
                    .builder()
                    .externalId(customer.getExternalId())
                    .firstName(customer.getFirstName())
                    .lastName(customer.getLastName())
                    .addresses(Collections.emptyList())
                    .createdDate(customer.getCreatedDate())
                    .createdBy(customer.getCreatedBy())
                    .build();
        } catch (Exception e) {
            LOGGER.error("Failed to create customer.", e);
            TransactionUtils.rollback(connection);
            throw new UseCaseException(ErrorType.CUSTOMER_NOT_CREATED, e.getMessage(), null, e);
        } finally {
            CloseableUtils.close(connection);
        }
    }
}
