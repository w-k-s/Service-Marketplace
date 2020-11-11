package com.wks.servicemarketplace.customerservice.adapters.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.wks.servicemarketplace.customerservice.adapters.auth.InvalidTokenException;
import com.wks.servicemarketplace.customerservice.adapters.auth.TokenValidator;
import com.wks.servicemarketplace.customerservice.core.usecase.UseCaseException;
import com.wks.servicemarketplace.customerservice.core.usecase.address.verifyaddress.VerifyAddressRequest;
import com.wks.servicemarketplace.customerservice.core.usecase.address.verifyaddress.VerifyAddressUseCase;
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
    public DefaultCustomerEventsReceiver(VerifyAddressUseCase verifyAddressUseCase,
                                         CreateCustomerUseCase createCustomerUseCase,
                                         TokenValidator tokenValidator,
                                         ObjectMapper objectMapper,
                                         Channel channel) {

        this.tokenValidator = tokenValidator;
        try {
            channel.exchangeDeclare(Exchange.ACCOUNT_EXCHANGE, BuiltinExchangeType.TOPIC, true, true, Collections.emptyMap());
            consumeVerifyAddress(verifyAddressUseCase, objectMapper, channel);
            consumeCustomerCreated(createCustomerUseCase, objectMapper, channel);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void consumeVerifyAddress(VerifyAddressUseCase verifyAddressUseCase, ObjectMapper objectMapper, Channel channel) throws IOException {
        channel.queueDeclare(QueueName.VERIFY_ADDRESS, true, false, true, Collections.emptyMap());

        final DeliverCallback deliverCallback = (consumerTag, message) -> {
            try {
                final VerifyAddressRequest request = objectMapper.readValue(message.getBody(), VerifyAddressRequest.class);
                verifyAddressUseCase.execute(request);
                channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
            } catch (UseCaseException e) {
                LOGGER.error(e.getMessage(), e);
            }
        };
        channel.basicConsume(QueueName.VERIFY_ADDRESS, false, deliverCallback, consumerTag -> { /*noop*/ });
    }

    private void consumeCustomerCreated(CreateCustomerUseCase createCustomerUseCase, ObjectMapper objectMapper, Channel channel) throws IOException {
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, Exchange.ACCOUNT_EXCHANGE, RoutingKey.CUSTOMER_ACCOUNT_CREATED);

        channel.basicConsume(queueName, false, (consumerTag, message) -> {
            try {
                final String token = message.getProperties().getHeaders().get("Authorization").toString().substring("Bearer".length()).trim();

                final CustomerRequest.Builder requestBuilder = objectMapper.readValue(message.getBody(), CustomerRequest.Builder.class);
                requestBuilder.authentication(tokenValidator.authenticate(token));

                createCustomerUseCase.execute(requestBuilder.build());
                channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
            } catch (UseCaseException | InvalidTokenException e) {
                LOGGER.error(e.getMessage(), e);
                // TODO: handle properly (e.g. put in error queue)
            }
        }, consumerTag -> { /*noop*/ });
    }
}