package com.wks.servicemarketplace.customerservice.config;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.wks.servicemarketplace.customerservice.core.utils.CloseableUtils;
import org.glassfish.hk2.api.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

public class AmqpConnectionFactory implements Factory<Connection> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AmqpConnectionFactory.class);

    private Connection connection;

    @Inject
    public AmqpConnectionFactory(ApplicationParameters applicationParameters) throws IOException, TimeoutException, NoSuchAlgorithmException, KeyManagementException, URISyntaxException {
        final var connectionFactory = new ConnectionFactory();
        connectionFactory.setUri(applicationParameters.amqpUri().replace("\u0020",""));
        this.connection = connectionFactory.newConnection();
    }

    @Override
    public Connection provide() {
        return connection;
    }

    @Override
    public void dispose(Connection instance) {
        CloseableUtils.close(instance, e -> LOGGER.error("Failed to dispose amqp connection", e));
    }
}
