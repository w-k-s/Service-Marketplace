package com.wks.servicemarketplace.accountservice.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.wks.servicemarketplace.accountservice.core.utils.CloseableUtils;
import org.glassfish.hk2.api.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;

public class AmqpChannelFactory implements Factory<Channel> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AmqpConnectionFactory.class);

    private Channel channel;

    @Inject
    public AmqpChannelFactory(Connection connection) throws IOException {
        this.channel = connection.createChannel();
    }

    @Override
    public Channel provide() {
        return channel;
    }

    @Override
    public void dispose(Channel instance) {
        CloseableUtils.close(instance, e -> LOGGER.error("Failed to dispose amqp channel", e));
    }
}
