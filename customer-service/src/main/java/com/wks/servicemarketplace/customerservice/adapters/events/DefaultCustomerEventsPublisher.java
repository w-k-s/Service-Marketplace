package com.wks.servicemarketplace.customerservice.adapters.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import com.wks.servicemarketplace.customerservice.core.events.CustomerEventsPublisher;
import com.wks.servicemarketplace.customerservice.messaging.AddressAddedEvent;
import com.wks.servicemarketplace.customerservice.messaging.CustomerCreatedEvent;
import com.wks.servicemarketplace.customerservice.messaging.CustomerMessaging;

import javax.inject.Inject;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.util.List;

public class DefaultCustomerEventsPublisher implements CustomerEventsPublisher {

    private Channel channel;
    private ObjectMapper objectMapper;

    @Inject
    public DefaultCustomerEventsPublisher(Channel amqpChannel, @Context ObjectMapper objectMapper) throws IOException {
        this.channel = amqpChannel;
        this.objectMapper = objectMapper;
    }

    @Override
    public void customerCreated(List<CustomerCreatedEvent> events) throws IOException {
        Preconditions.checkNotNull(events);

        for (CustomerCreatedEvent event : events) {
            channel.basicPublish(
                    CustomerMessaging.Exchange.MAIN,
                    CustomerMessaging.RoutingKey.CUSTOMER_PROFILE_CREATED,
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
                    CustomerMessaging.Exchange.MAIN,
                    CustomerMessaging.RoutingKey.ADDRESS_ADDED,
                    MessageProperties.PERSISTENT_TEXT_PLAIN,
                    objectMapper.writeValueAsBytes(event)
            );
        }
    }
}
