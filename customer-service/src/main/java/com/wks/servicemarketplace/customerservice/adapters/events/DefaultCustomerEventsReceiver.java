package com.wks.servicemarketplace.customerservice.adapters.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.wks.servicemarketplace.customerservice.adapters.auth.InvalidTokenException;
import com.wks.servicemarketplace.customerservice.adapters.auth.TokenValidator;
import com.wks.servicemarketplace.customerservice.core.exceptions.CoreException;
import com.wks.servicemarketplace.customerservice.core.usecase.customer.CreateCustomerUseCase;
import com.wks.servicemarketplace.customerservice.core.usecase.customer.CustomerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Collections;

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
            channel.exchangeDeclare(Exchange.AUTH_EXCHANGE, BuiltinExchangeType.TOPIC, true, true, Collections.emptyMap());
            consumeCustomerCreated(createCustomerUseCase, objectMapper, channel);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void consumeCustomerCreated(CreateCustomerUseCase createCustomerUseCase, ObjectMapper objectMapper, Channel channel) throws IOException {
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, Exchange.AUTH_EXCHANGE, RoutingKey.Incoming.CUSTOMER_ACCOUNT_CREATED);

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