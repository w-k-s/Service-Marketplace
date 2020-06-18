package com.wks.servicemarketplace.accountservice.adapters.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import com.wks.servicemarketplace.accountservice.core.events.CustomerEventsPublisher;
import com.wks.servicemarketplace.accountservice.core.models.events.AddressAddedEvent;
import com.wks.servicemarketplace.accountservice.core.models.events.CustomerCreatedEvent;

import javax.inject.Inject;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.util.List;

public class DefaultCustomerEventsPublisher implements CustomerEventsPublisher {

    private static final String QUEUE_CUSTOMER_CREATED = "com.wks.servicemarketplace.account.customer.created";
    private static final String QUEUE_ADDRESS_ADDED = "com.wks.servicemarketplace.account.customer.address.added";

    private Channel channel;
    private ObjectMapper objectMapper;

    @Inject
    public DefaultCustomerEventsPublisher(Channel amqpChannel, @Context ObjectMapper objectMapper) {
        this.channel = amqpChannel;
        this.objectMapper = objectMapper;
    }

    @Override
    public void customerCreated(List<CustomerCreatedEvent> events) throws IOException {
        Preconditions.checkNotNull(events);

        channel.queueDeclare(QUEUE_CUSTOMER_CREATED, true, false, false, null);

        for (CustomerCreatedEvent event : events) {
            channel.basicPublish(
                    "",
                    QUEUE_CUSTOMER_CREATED,
                    MessageProperties.PERSISTENT_TEXT_PLAIN,
                    objectMapper.writeValueAsBytes(event)
            );
        }
    }

    @Override
    public void addressAdded(List<AddressAddedEvent> events) throws IOException {
        Preconditions.checkNotNull(events);

        channel.queueDeclare(QUEUE_ADDRESS_ADDED, true, false, false, null);

        for (AddressAddedEvent event : events) {
            channel.basicPublish(
                    "",
                    QUEUE_ADDRESS_ADDED,
                    MessageProperties.PERSISTENT_TEXT_PLAIN,
                    objectMapper.writeValueAsBytes(event)
            );
        }
    }
}
