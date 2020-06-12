package com.wks.servicemarketplace.accountservice;

import com.wks.servicemarketplace.accountservice.adapters.db.dao.DataSource;
import com.wks.servicemarketplace.accountservice.adapters.db.dao.DefaultCustomerDao;
import com.wks.servicemarketplace.accountservice.adapters.web.errors.exceptionmappers.UseCaseExceptionMapper;
import com.wks.servicemarketplace.accountservice.adapters.web.resources.AddressResource;
import com.wks.servicemarketplace.accountservice.adapters.web.resources.CustomerResource;
import com.wks.servicemarketplace.accountservice.config.ApplicationParameters;
import com.wks.servicemarketplace.accountservice.config.ObjectMapperProvider;
import com.wks.servicemarketplace.accountservice.core.daos.CustomerDao;
import com.wks.servicemarketplace.accountservice.core.usecase.customer.AddAddressUseCase;
import com.wks.servicemarketplace.accountservice.core.usecase.customer.CreateCustomerUseCase;
import com.wks.servicemarketplace.accountservice.core.utils.CloseableUtils;
import org.eclipse.jetty.server.Server;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.Provider;
import java.io.Closeable;
import java.net.URI;

@Provider
public class AccountServiceApplication extends ResourceConfig implements Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountServiceApplication.class.getSimpleName());

    private DataSource dataSource;

    public AccountServiceApplication() {
        ApplicationParameters parameters = loadParameters();
        registerResources(parameters);
    }

    private ApplicationParameters loadParameters() {
        return ApplicationParameters.builder()
                .host(System.getenv("serverHost"))
                .port(System.getenv("serverPort"))
                .jdbcUrl(System.getenv("jdbcUrl"))
                .jdbcUsername(System.getenv("jdbcUsername"))
                .jdbcPassword(System.getenv("jdbcPassword"))
                .build();
    }

    private void registerResources(ApplicationParameters parameters) {
        this.dataSource = new DataSource(parameters.getJdbcUrl(), parameters.getJdbcUsername(), parameters.getJdbcPassword());

        register(UseCaseExceptionMapper.class);
        register(ObjectMapperProvider.class);
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(new DefaultCustomerDao(dataSource)).to(CustomerDao.class);
                bind(CreateCustomerUseCase.class).to(CreateCustomerUseCase.class);
                bind(AddAddressUseCase.class).to(AddAddressUseCase.class);
            }
        });
        register(CustomerResource.class);
        register(AddressResource.class);
    }

    public void run() throws InterruptedException {
        final URI uri = UriBuilder.fromUri("http://0.0.0.0/").port(8080).build();

        final Server server = JettyHttpContainerFactory.createServer(uri, this);
        try {
            LOGGER.info("Started listening on {}:{}", uri.getHost(), uri.getPort());
            server.join();
        } finally {
            server.destroy();
        }
    }

    @Override
    public void close() {
        LOGGER.info("Closing application");
        CloseableUtils.close(dataSource, it -> LOGGER.error("Failed to close datasource", it));
    }

    public static void main(String[] args) {
        AccountServiceApplication application = new AccountServiceApplication();
        try {
            application.run();
        } catch (Exception e) {
            LOGGER.error("Application execution failed", e);
        } finally {
            CloseableUtils.close(application);
        }
    }
}
