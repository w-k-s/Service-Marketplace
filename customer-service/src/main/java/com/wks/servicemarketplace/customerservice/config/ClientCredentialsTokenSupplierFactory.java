package com.wks.servicemarketplace.customerservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wks.servicemarketplace.authservice.api.ClientCredentialsRequest;
import com.wks.servicemarketplace.authservice.api.ClientCredentialsTokenSupplier;
import org.glassfish.hk2.api.Factory;

import javax.inject.Inject;
import java.util.Collections;

public class ClientCredentialsTokenSupplierFactory implements Factory<ClientCredentialsTokenSupplier> {

    private ClientCredentialsTokenSupplier clientCredentialsTokenSupplier;

    @Inject
    public ClientCredentialsTokenSupplierFactory(ApplicationParameters applicationParameters, ObjectMapper objectMapper){
        clientCredentialsTokenSupplier = new ClientCredentialsTokenSupplier(
                new ClientCredentialsRequest(applicationParameters.getClientId(), applicationParameters.getClientSecret()),
                applicationParameters.getAuthServiceBaseUrl(),
                objectMapper
        );
    }

    @Override
    public ClientCredentialsTokenSupplier provide() {
        return clientCredentialsTokenSupplier;
    }

    @Override
    public void dispose(ClientCredentialsTokenSupplier instance) {

    }
}
