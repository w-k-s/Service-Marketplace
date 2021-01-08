package com.wks.servicemarketplace.customerservice.adapters.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import com.wks.servicemarketplace.common.messaging.Message;
import com.wks.servicemarketplace.customerservice.messaging.CustomerMessaging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;

public class DefaultMessagePublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMessagePublisher.class);

    private Channel channel;

    @Inject
    public DefaultMessagePublisher(Channel amqpChannel) throws IOException {
        this.channel = amqpChannel;
        CustomerMessaging.Exchange.MAIN.declare(amqpChannel);
    }

    public boolean publish(Message message, String token) throws IOException {
        Preconditions.checkNotNull(message);


        try {
            channel.basicPublish(
                    message.getDestinationExchange(),
                    message.getDestinationRoutingKey(),
                    MessageProperties.PERSISTENT_TEXT_PLAIN.builder()
                            .correlationId(message.getCorrelationId())
                            .messageId(message.getId().toString())
                            .replyTo(message.getReplyQueue())
                            .headers(ImmutableMap.of("Authorization", String.format("Bearer %s", token)))
                            .build(),
                    message.getPayload().getBytes()
            );
            return true;
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to publish message", e);
            return false;
        }
    }
}
