package com.wks.servicemarketplace.customerservice.adapters.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.wks.servicemarketplace.authservice.messaging.AccountCreatedEvent;
import com.wks.servicemarketplace.authservice.messaging.AuthMessaging;
import com.wks.servicemarketplace.common.auth.TokenValidator;
import com.wks.servicemarketplace.customerservice.api.CustomerRequest;
import com.wks.servicemarketplace.customerservice.core.usecase.customer.CreateCustomerUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;

public class DefaultCustomerEventsReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCustomerEventsReceiver.class);
    private final TokenValidator tokenValidator;
    private final ObjectMapper objectMapper;
    private final Channel channel;

    @Inject
    public DefaultCustomerEventsReceiver(CreateCustomerUseCase createCustomerUseCase,
                                         TokenValidator tokenValidator,
                                         ObjectMapper objectMapper,
                                         Channel channel) {

        this.tokenValidator = tokenValidator;
        this.objectMapper = objectMapper;
        this.channel = channel;

        consumeCustomerCreated(createCustomerUseCase);
    }

    private void consumeCustomerCreated(CreateCustomerUseCase createCustomerUseCase) {
        try {
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, AuthMessaging.Exchange.MAIN, AuthMessaging.RoutingKey.CUSTOMER_ACCOUNT_CREATED);
            channel.basicConsume(queueName, false, (consumerTag, message) -> {

                final var token = message.getProperties().getHeaders().get("Authorization").toString().substring("Bearer".length()).trim();
                final var accountCreatedEvent = objectMapper.readValue(message.getBody(), AccountCreatedEvent.class);
                final var customerRequest = CustomerRequest.builder()
                        .userId(accountCreatedEvent.getUuid())
                        .firstName(accountCreatedEvent.getName().getFirstName())
                        .lastName(accountCreatedEvent.getName().getLastName())
                        .email(accountCreatedEvent.getEmail().toString())
                        .correlationId(message.getProperties().getCorrelationId())
                        .authentication(tokenValidator.authenticate(token))
                        .build();

                LOGGER.info("Received create customer request: {}", customerRequest);
                createCustomerUseCase.execute(customerRequest);
                channel.basicAck(message.getEnvelope().getDeliveryTag(), false);

            }, consumerTag -> { /*noop*/ });
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}