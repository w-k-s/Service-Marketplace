package com.wks.servicemarketplace.customerservice.adapters.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.wks.servicemarketplace.authservice.messaging.AuthMessaging;
import com.wks.servicemarketplace.common.auth.TokenValidator;
import com.wks.servicemarketplace.common.errors.CoreException;
import com.wks.servicemarketplace.common.errors.InvalidTokenException;
import com.wks.servicemarketplace.customerservice.api.CustomerRequest;
import com.wks.servicemarketplace.customerservice.core.usecase.customer.CreateCustomerUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;

public class DefaultCustomerEventsReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCustomerEventsReceiver.class);
    private final TokenValidator tokenValidator;

    @Inject
    public DefaultCustomerEventsReceiver(CreateCustomerUseCase createCustomerUseCase,
                                         TokenValidator tokenValidator,
                                         ObjectMapper objectMapper,
                                         Channel channel) {

        this.tokenValidator = tokenValidator;
        try {
            consumeCustomerCreated(createCustomerUseCase, objectMapper, channel);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void consumeCustomerCreated(CreateCustomerUseCase createCustomerUseCase, ObjectMapper objectMapper, Channel channel) throws IOException {
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, AuthMessaging.Exchange.MAIN, AuthMessaging.RoutingKey.CUSTOMER_ACCOUNT_CREATED);

        channel.basicConsume(queueName, false, (consumerTag, message) -> {
            try {
                final String token = message.getProperties().getHeaders().get("Authorization").toString().substring("Bearer".length()).trim();

                final CustomerRequest customerRequest = objectMapper.readValue(message.getBody(), CustomerRequest.Builder.class)
                        .authentication(tokenValidator.authenticate(token))
                        .build();

                createCustomerUseCase.execute(customerRequest);
                channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
            } catch (CoreException | InvalidTokenException e) {
                LOGGER.error(e.getMessage(), e);
                // TODO: handle properly (e.g. put in error queue)
            }
        }, consumerTag -> { /*noop*/ });
    }
}