package com.wks.servicemarketplace.accountservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.wks.servicemarketplace.accountservice.adapters.db.dao.DataSource;
import com.wks.servicemarketplace.accountservice.adapters.db.dao.DefaultCustomerDao;
import com.wks.servicemarketplace.accountservice.adapters.events.DefaultCustomerEventsPublisher;
import com.wks.servicemarketplace.accountservice.adapters.events.DefaultVerifyAddressEventReceiver;
import com.wks.servicemarketplace.accountservice.adapters.web.errors.exceptionmappers.UseCaseExceptionMapper;
import com.wks.servicemarketplace.accountservice.adapters.web.resources.AddressResource;
import com.wks.servicemarketplace.accountservice.adapters.web.resources.CustomerResource;
import com.wks.servicemarketplace.accountservice.config.*;
import com.wks.servicemarketplace.accountservice.core.daos.CustomerDao;
import com.wks.servicemarketplace.accountservice.core.events.CustomerEventsPublisher;
import com.wks.servicemarketplace.accountservice.core.usecase.address.AddAddressUseCase;
import com.wks.servicemarketplace.accountservice.core.usecase.customer.CreateCustomerUseCase;
import com.wks.servicemarketplace.accountservice.core.usecase.address.verifyaddress.VerifyAddressUseCase;
import org.eclipse.jetty.server.Server;
import org.glassfish.hk2.api.Immediate;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class AccountServiceApplication extends ResourceConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountServiceApplication.class.getSimpleName());

    public AccountServiceApplication() {
        registerResources();
    }

    private void registerResources() {
        register(ImmediateFeature.class);
        register(UseCaseExceptionMapper.class);
        register(ObjectMapperProvider.class);
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bindFactory(ApplicationParametersFactory.class, Singleton.class).to(ApplicationParameters.class).in(Singleton.class);
                bindFactory(ObjectMapperFactory.class, Singleton.class).to(ObjectMapper.class).in(Singleton.class);
                bindFactory(DataSourceFactory.class, Singleton.class).to(DataSource.class).in(Singleton.class);
                bindFactory(AmqpConnectionFactory.class, Singleton.class).to(Connection.class).in(Singleton.class);
                bindFactory(AmqpChannelFactory.class, Singleton.class).to(Channel.class).in(Singleton.class);
                bindFactory(DefaultVerifyAddressEventReceiverFactory.class, Immediate.class).to(DefaultVerifyAddressEventReceiver.class).in(Immediate.class);
                bindFactory(VerifyAddressUseCaseFactory.class, Immediate.class).to(VerifyAddressUseCase.class).in(Immediate.class);
                bind(DefaultCustomerDao.class).to(CustomerDao.class);
                bind(DefaultCustomerEventsPublisher.class).to(CustomerEventsPublisher.class);
                bind(CreateCustomerUseCase.class).to(CreateCustomerUseCase.class);
                bind(AddAddressUseCase.class).to(AddAddressUseCase.class);
                bind(VerifyAddressUseCase.class).to(VerifyAddressUseCase.class);
            }
        });
        register(CustomerResource.class);
        register(AddressResource.class);
    }

    public void run() throws InterruptedException {
        final URI uri = UriBuilder
                .fromUri(System.getenv("serverHost"))
                .port(Integer.parseInt(System.getenv("serverPort")))
                .build();

        final Server server = JettyHttpContainerFactory.createServer(uri, this);
        try {
            LOGGER.info("Started listening on {}:{}", uri.getHost(), uri.getPort());
            server.join();
        } finally {
            server.destroy();
        }
    }

    public static void main(String[] args) {
        try {
            AccountServiceApplication application = new AccountServiceApplication();
            application.run();
        } catch (Exception e) {
            LOGGER.error("Application execution failed", e);
        }
    }
}
