package com.wks.servicemarketplace.authservice.config

import org.glassfish.hk2.api.Factory

class ApplicationParametersFactory : Factory<ApplicationParameters> {

    private val applicationParameters: ApplicationParameters = ApplicationParameters(
            KeycloakConfiguration(
                    serverUrl = System.getenv("keycloakServerUrl"),
                    realm = System.getenv("keycloakRealm"),
                    adminId = System.getenv("keycloakAdminId"),
                    adminSecret = System.getenv("keycloakAdminSecret"),
                    clientId = System.getenv("keycloakClientId"),
                    clientSecret = System.getenv("keycloakClientSecret")
            )
    );

    override fun provide() = applicationParameters

    override fun dispose(instance: ApplicationParameters?) {
    }
}