package com.wks.servicemarketplace.customerservice.adapters.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import com.wks.servicemarketplace.customerservice.core.events.CustomerEventsPublisher;
import com.wks.servicemarketplace.customerservice.core.usecase.address.AddressAddedEvent;
import com.wks.servicemarketplace.customerservice.core.usecase.customer.CustomerCreatedEvent;
import com.wks.servicemarketplace.customerservice.core.usecase.address.verifyaddress.AddressVerificationFailedEvent;
import com.wks.servicemarketplace.customerservice.core.usecase.address.verifyaddress.AddressVerifiedEvent;

import javax.inject.Inject;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class DefaultCustomerEventsPublisher implements CustomerEventsPublisher {

    private Channel channel;
    private ObjectMapper objectMapper;

    @Inject
    public DefaultCustomerEventsPublisher(Channel amqpChannel, @Context ObjectMapper objectMapper) throws IOException {
        this.channel = amqpChannel;
        this.objectMapper = objectMapper;

        channel.exchangeDeclare(Exchange.CUSTOMER_EXCHANGE, BuiltinExchangeType.TOPIC, true, true, Collections.emptyMap());
    }

    @Override
    public void customerCreated(List<CustomerCreatedEvent> events) throws IOException {
        Preconditions.checkNotNull(events);

        for (CustomerCreatedEvent event : events) {
            channel.basicPublish(
                    Exchange.CUSTOMER_EXCHANGE,
                    RoutingKey.CUSTOMER_PROFILE_CREATED,
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
                    Exchange.CUSTOMER_EXCHANGE,
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
