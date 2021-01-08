package com.wks.servicemarketplace.customerservice.core.usecase.customer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wks.servicemarketplace.common.CustomerUUID;
import com.wks.servicemarketplace.common.Name;
import com.wks.servicemarketplace.common.errors.CoreException;
import com.wks.servicemarketplace.common.errors.CoreThrowable;
import com.wks.servicemarketplace.common.events.EventEnvelope;
import com.wks.servicemarketplace.common.messaging.Message;
import com.wks.servicemarketplace.common.messaging.MessageId;
import com.wks.servicemarketplace.customerservice.api.CustomerRequest;
import com.wks.servicemarketplace.customerservice.api.CustomerResponse;
import com.wks.servicemarketplace.customerservice.core.auth.AuthorizationUtils;
import com.wks.servicemarketplace.customerservice.core.daos.CustomerDao;
import com.wks.servicemarketplace.customerservice.core.daos.EventDao;
import com.wks.servicemarketplace.customerservice.core.daos.OutboxDao;
import com.wks.servicemarketplace.customerservice.core.daos.TransactionUtils;
import com.wks.servicemarketplace.customerservice.core.usecase.ResultWithEvents;
import com.wks.servicemarketplace.customerservice.core.usecase.UseCase;
import com.wks.servicemarketplace.customerservice.core.utils.CloseableUtils;
import com.wks.servicemarketplace.customerservice.messaging.CustomerCreatedEvent;
import com.wks.servicemarketplace.customerservice.messaging.CustomerCreationFailedEvent;
import com.wks.servicemarketplace.customerservice.messaging.CustomerMessaging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;

public class CreateCustomerUseCase implements UseCase<CustomerRequest, CustomerResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateCustomerUseCase.class);

    private final CustomerDao customerDao;
    private final EventDao eventDao;
    private final OutboxDao outboxDao;
    private final ObjectMapper objectMapper;

    @Inject
    public CreateCustomerUseCase(CustomerDao customerDao,
                                 EventDao eventDao,
                                 OutboxDao outboxDao,
                                 ObjectMapper objectMapper) {
        this.customerDao = customerDao;
        this.eventDao = eventDao;
        this.outboxDao = outboxDao;
        this.objectMapper = objectMapper;
    }

    @Override
    public CustomerResponse execute(CustomerRequest customerRequest) {
        Connection connection = null;
        try {
            AuthorizationUtils.checkRole(customerRequest.getAuthentication(), "customer.create");
            connection = TransactionUtils.beginTransaction(customerDao.getConnection());

            ResultWithEvents<Customer, CustomerCreatedEvent> customerAndEvents = Customer.create(
                    customerDao.newCustomerExternalId(connection),
                    CustomerUUID.of(customerRequest.getUserId()),
                    Name.of(customerRequest.getFirstName(), customerRequest.getLastName()),
                    customerRequest.getEmail()
            );

            final var customer = customerAndEvents.getResult();
            customerDao.saveCustomer(connection, customer);

            final var customerCreatedEvent = customerAndEvents.firstEvent();
            publishCustomerCreated(connection, customerRequest, customerCreatedEvent);

            connection.commit();

            return CustomerResponse
                    .builder()
                    .uuid(customer.getUuid())
                    .externalId(customer.getExternalId())
                    .name(customer.getName())
                    .addresses(Collections.emptyList())
                    .version(customer.getVersion())
                    .build();
        } catch (CoreException e) {
            LOGGER.error("Failed to create customer.", e);
            TransactionUtils.rollback(connection);
            publishCustomerCreationFailed(connection, customerRequest, e);
            throw e;
        } catch (SQLException e) {
            LOGGER.error("Failed to create customer.", e);
            TransactionUtils.rollback(connection);
            throw new RuntimeException(e);
        } finally {
            CloseableUtils.close(connection);
        }
    }

    private void publishCustomerCreated(Connection connection,
                                        CustomerRequest request,
                                        CustomerCreatedEvent event) {

        final String payload;
        try {
            payload = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        eventDao.saveEvent(connection, new EventEnvelope(
                event,
                event.getUuid().toString(),
                payload
        ));

        request.getCorrelationId().ifPresent(correlationId -> {
            outboxDao.saveMessage(connection, new Message(
                    MessageId.random(),
                    event.getEventType().toString(),
                    payload,
                    CustomerMessaging.Exchange.MAIN.exchangeName,
                    false,
                    correlationId,
                    CustomerMessaging.RoutingKey.CUSTOMER_PROFILE_CREATED,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            ));
        });
    }

    private void publishCustomerCreationFailed(Connection connection, CustomerRequest customerRequest, CoreThrowable error) {
        customerRequest.getCorrelationId().ifPresent(correlationId -> {
            var profileCreationFailed = new CustomerCreationFailedEvent(error);

            final String payload;
            try {
                payload = objectMapper.writeValueAsString(profileCreationFailed);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            outboxDao.saveMessage(connection, new Message(
                    MessageId.random(),
                    profileCreationFailed.getEventType().toString(),
                    payload,
                    CustomerMessaging.Exchange.MAIN.exchangeName,
                    false,
                    correlationId,
                    CustomerMessaging.RoutingKey.CUSTOMER_PROFILE_CREATION_FAILED,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            ));
        });
    }
}
