package com.wks.servicemarketplace.customerservice.messaging;

import com.google.common.base.Preconditions;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.Collections;

public class CustomerMessaging {
    public enum Exchange {
        MAIN("com.wks.servicemarketplace.customer.exchange.main");

        public final String exchangeName;

        Exchange(String exchangeName) {
            this.exchangeName = exchangeName;
        }

        public void declare(Channel channel) throws IOException {
            Preconditions.checkNotNull(channel);
            channel.exchangeDeclare(this.exchangeName, BuiltinExchangeType.TOPIC, true, false, false, Collections.emptyMap());
        }
    }

    public static class RoutingKey {
        public static final String ADDRESS_ADDED = "com.wks.servicemarketplace.customer.address.create.success";
        public static final String CUSTOMER_PROFILE_CREATED = "com.wks.servicemarketplace.customer.profile.create.success";
        public static final String CUSTOMER_PROFILE_CREATION_FAILED = "com.wks.servicemarketplace.customer.profile.create.failure";
    }
}
