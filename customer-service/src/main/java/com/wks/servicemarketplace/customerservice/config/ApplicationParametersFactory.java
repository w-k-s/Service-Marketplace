package com.wks.servicemarketplace.customerservice.config;

import org.glassfish.hk2.api.Factory;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

@Provider
public class ApplicationParametersFactory implements Factory<ApplicationParameters> {

    private ApplicationParameters applicationParameters;

    @Inject
    public ApplicationParametersFactory() {
        this.applicationParameters = ApplicationParameters.builder()
                .serverHost(System.getenv("serverHost"))
                .serverPort(System.getenv("serverPort"))
                .jdbcUrl(System.getenv("jdbcUrl"))
                .jdbcUsername(System.getenv("jdbcUsername"))
                .jdbcPassword(System.getenv("jdbcPassword"))
                .amqpHost(System.getenv("amqpHost"))
                .amqpPort(Integer.parseInt(System.getenv("amqpPort")))
                .keycloakKeySetUrl(System.getenv("keycloakKeySetUrl"))
                .build();
    }

    @Override
    public ApplicationParameters provide() {
        return applicationParameters;
    }

    @Override
    public void dispose(ApplicationParameters instance) {
    }
}
