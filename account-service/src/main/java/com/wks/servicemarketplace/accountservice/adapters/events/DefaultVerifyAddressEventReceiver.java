package com.wks.servicemarketplace.accountservice.adapters.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.wks.servicemarketplace.accountservice.core.usecase.UseCaseException;
import com.wks.servicemarketplace.accountservice.core.usecase.address.verifyaddress.VerifyAddressRequest;
import com.wks.servicemarketplace.accountservice.core.usecase.address.verifyaddress.VerifyAddressUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Collections;

public class DefaultVerifyAddressEventReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultVerifyAddressEventReceiver.class);

    @Inject
    public DefaultVerifyAddressEventReceiver(VerifyAddressUseCase verifyAddressUseCase, ObjectMapper objectMapper, Channel channel) {
        try {
            LOGGER.info("DefaultVerifyAddressEventReceiver");
            consumeVerifyAddress(verifyAddressUseCase, objectMapper, channel);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void consumeVerifyAddress(VerifyAddressUseCase verifyAddressUseCase, ObjectMapper objectMapper, Channel channel) throws IOException {
        LOGGER.info("consumeVerifyAddress");
        channel.queueDeclare(QueueName.VERIFY_ADDRESS, true, false, true, Collections.emptyMap());

        final DeliverCallback deliverCallback = (consumerTag, message) -> {
            try {
                LOGGER.info("deliverCallback");
                final VerifyAddressRequest request = objectMapper.readValue(message.getBody(), VerifyAddressRequest.class);
                verifyAddressUseCase.execute(request);
                channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
            } catch (UseCaseException e) {
                LOGGER.error(e.getMessage(), e);
            }
        };
        channel.basicConsume(QueueName.VERIFY_ADDRESS, false, deliverCallback, consumerTag -> {
        });
    }
}
