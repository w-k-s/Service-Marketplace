package com.wks.servicemarketplace.accountservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wks.servicemarketplace.accountservice.adapters.auth.TokenValidator;
import com.wks.servicemarketplace.accountservice.adapters.auth.keycloak.KeycloakJwksValidator;
import org.glassfish.hk2.api.Factory;

import javax.inject.Inject;

public class TokenValidatorFactory implements Factory<TokenValidator> {

    private final KeycloakJwksValidator tokenValidator;

    @Inject
    public TokenValidatorFactory(ApplicationParameters applicationParameters,
                                 ObjectMapper objectMapper) throws Exception {
        tokenValidator = KeycloakJwksValidator.withKeySetFromURL(
                applicationParameters.getKeycloakKeySetUrl(),
                objectMapper
        );
    }

    @Override
    public TokenValidator provide() {
        return tokenValidator;
    }

    @Override
    public void dispose(TokenValidator instance) {

    }
}
