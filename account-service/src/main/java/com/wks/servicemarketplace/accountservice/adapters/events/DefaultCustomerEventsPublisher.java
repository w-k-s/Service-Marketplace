package com.wks.servicemarketplace.accountservice.adapters.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import com.wks.servicemarketplace.accountservice.core.events.CustomerEventsPublisher;
import com.wks.servicemarketplace.accountservice.core.models.events.AddressAddedEvent;
import com.wks.servicemarketplace.accountservice.core.models.events.CustomerCreatedEvent;
import com.wks.servicemarketplace.accountservice.core.usecase.address.verifyaddress.AddressVerificationFailedEvent;
import com.wks.servicemarketplace.accountservice.core.usecase.address.verifyaddress.AddressVerifiedEvent;

import javax.inject.Inject;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class DefaultCustomerEventsPublisher implements CustomerEventsPublisher {

    private static final String EXCHANGE_NAME = "com.wks.servicemarketplace.account.exchange";

    private Channel channel;
    private ObjectMapper objectMapper;

    @Inject
    public DefaultCustomerEventsPublisher(Channel amqpChannel, @Context ObjectMapper objectMapper) throws IOException {
        this.channel = amqpChannel;
        this.objectMapper = objectMapper;

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC, true, true, Collections.emptyMap());
    }

    @Override
    public void customerCreated(List<CustomerCreatedEvent> events) throws IOException {
        Preconditions.checkNotNull(events);

        for (CustomerCreatedEvent event : events) {
            channel.basicPublish(
                    EXCHANGE_NAME,
                    RoutingKey.CUSTOMER_CREATED,
                    MessageProperties.PERSISTENT_TEXT_PLAIN,
                    objectMapper.writeValueAsBytes(event)
            );
        }
    }

    @Override
    public void addressAdded(List<AddressAddedEvent> events) throws IOException {
        Preconditions.checkNotNull(events);

        for (AddressAddedEvent event : events) {
            channel.basicPublish(
                    EXCHANGE_NAME,
                    RoutingKey.ADDRESS_ADDED,
                    MessageProperties.PERSISTENT_TEXT_PLAIN,
                    objectMapper.writeValueAsBytes(event)
            );
        }
    }

    @Override
    public void addressVerified(AddressVerifiedEvent event) throws IOException {

        channel.queueDeclare(QueueName.ADDRESS_VERIFIED, true, false, true, Collections.emptyMap());

        channel.basicPublish(
                "",
                QueueName.ADDRESS_VERIFIED,
                MessageProperties.PERSISTENT_TEXT_PLAIN,
                objectMapper.writeValueAsBytes(event)
        );
    }

    @Override
    public void addressVerificationFailed(AddressVerificationFailedEvent event) throws IOException {
        channel.queueDeclare(QueueName.ADDRESS_VERIFICATION_FAILED, true, false, true, Collections.emptyMap());

        channel.basicPublish(
                "",
                QueueName.ADDRESS_VERIFICATION_FAILED,
                MessageProperties.PERSISTENT_TEXT_PLAIN,
                objectMapper.writeValueAsBytes(event)
        );
    }
}
