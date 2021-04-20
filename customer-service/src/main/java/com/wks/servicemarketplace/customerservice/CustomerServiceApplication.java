package com.wks.servicemarketplace.customerservice;

import com.codahale.metrics.health.HealthCheckRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.wks.servicemarketplace.authservice.api.ClientCredentialsTokenSupplier;
import com.wks.servicemarketplace.common.auth.TokenValidator;
import com.wks.servicemarketplace.customerservice.adapters.db.dao.DataSource;
import com.wks.servicemarketplace.customerservice.adapters.db.dao.DefaultCustomerDao;
import com.wks.servicemarketplace.customerservice.adapters.db.dao.DefaultEventDao;
import com.wks.servicemarketplace.customerservice.adapters.db.dao.DefaultOutboxDao;
import com.wks.servicemarketplace.customerservice.adapters.events.DefaultCustomerEventsReceiver;
import com.wks.servicemarketplace.customerservice.adapters.events.DefaultMessagePublisher;
import com.wks.servicemarketplace.customerservice.adapters.events.TransactionalOutboxJobFactory;
import com.wks.servicemarketplace.customerservice.adapters.web.ApiResource;
import com.wks.servicemarketplace.customerservice.adapters.web.DefaultExceptionMapper;
import com.wks.servicemarketplace.customerservice.adapters.web.HealthResource;
import com.wks.servicemarketplace.customerservice.config.*;
import com.wks.servicemarketplace.customerservice.config.healthchecks.HealthChecksFactory;
import com.wks.servicemarketplace.customerservice.config.schedulers.Schedulers;
import com.wks.servicemarketplace.customerservice.config.schedulers.SchedulersFactory;
import com.wks.servicemarketplace.customerservice.core.daos.CustomerDao;
import com.wks.servicemarketplace.customerservice.core.daos.EventDao;
import com.wks.servicemarketplace.customerservice.core.daos.OutboxDao;
import com.wks.servicemarketplace.customerservice.core.usecase.address.AddAddressUseCase;
import com.wks.servicemarketplace.customerservice.core.usecase.address.FindAddressByCustomerUuidUseCase;
import com.wks.servicemarketplace.customerservice.core.usecase.customer.CreateCustomerUseCase;
import com.wks.servicemarketplace.customerservice.core.usecase.customer.GetCustomerUseCase;
import org.eclipse.jetty.server.Server;
import org.glassfish.hk2.api.Immediate;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class CustomerServiceApplication extends ResourceConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerServiceApplication.class.getSimpleName());
    private final ApplicationParameters parameters;

    public CustomerServiceApplication(ApplicationParameters applicationParameters) {
        parameters = applicationParameters;
        registerResources();
    }

    private void registerResources() {
        register(ImmediateFeature.class);
        register(ObjectMapperProvider.class);
        register(AuthenticationFilter.class);
        register(DefaultExceptionMapper.class);
        register(ApiResource.class);
        register(HealthResource.class);
        register(DefaultApplicationEventListener.class);
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bindFactory(ObjectMapperFactory.class, Immediate.class).to(ObjectMapper.class).in(Immediate.class);
                bindFactory(DataSourceFactory.class, Immediate.class).to(DataSource.class).in(Immediate.class);
                bindFactory(AmqpConnectionFactory.class, Immediate.class).to(Connection.class).in(Immediate.class);
                bindFactory(AmqpChannelFactory.class, Immediate.class).to(Channel.class).in(Immediate.class);
                bindFactory(DefaultCustomerEventsReceiverFactory.class, Immediate.class).to(DefaultCustomerEventsReceiver.class).in(Immediate.class);
                bindFactory(HealthChecksFactory.class, Immediate.class).to(HealthCheckRegistry.class).in(Immediate.class);
                bindFactory(TokenValidatorFactory.class, Immediate.class).to(TokenValidator.class).in(Immediate.class);
                bindFactory(ClientCredentialsTokenSupplierFactory.class, Immediate.class).to(ClientCredentialsTokenSupplier.class).in(Immediate.class);
                bindFactory(SchedulersFactory.class, Immediate.class).to(Schedulers.class).in(Immediate.class);

                bind(parameters).to(ApplicationParameters.class);
                bind(DefaultCustomerDao.class).to(CustomerDao.class).in(Immediate.class);
                bind(DefaultOutboxDao.class).to(OutboxDao.class).in(Immediate.class);
                bind(DefaultEventDao.class).to(EventDao.class).in(Immediate.class);
                bind(DatabaseMigration.class).to(DatabaseMigration.class).in(Immediate.class);
                bind(GetCustomerUseCase.class).to(GetCustomerUseCase.class).in(Immediate.class);
                bind(AddAddressUseCase.class).to(AddAddressUseCase.class).in(Immediate.class);
                bind(CreateCustomerUseCase.class).to(CreateCustomerUseCase.class).in(Immediate.class);
                bind(DefaultMessagePublisher.class).to(DefaultMessagePublisher.class).in(Immediate.class);
                bind(FindAddressByCustomerUuidUseCase.class).to(FindAddressByCustomerUuidUseCase.class).in(Immediate.class);
                bind(TransactionalOutboxJobFactory.class).to(TransactionalOutboxJobFactory.class).in(Immediate.class);
            }
        });

    }

    public void run() throws Exception {
        final URI uri = URI.create(String.format("http://%s:%s/", parameters.serverHost(), parameters.serverPort()));

        final Server server = JettyHttpContainerFactory.createServer(uri, this, false);

        try {
            LOGGER.info("Started listening on {}:{}", uri.getHost(), uri.getPort());
            server.start();
            server.join();
        } finally {
            server.destroy();
        }
    }

    public static void main(String[] args) {
        try {
            final var application = new CustomerServiceApplication(ApplicationParameters.load());
            application.run();
        } catch (Exception e) {
            LOGGER.error("Application execution failed", e);
        }
    }
}
