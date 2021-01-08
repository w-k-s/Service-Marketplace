package com.wks.servicemarketplace.authservice.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.wks.servicemarketplace.authservice.api.ClientCredentialsRequest
import com.wks.servicemarketplace.authservice.api.ClientCredentialsTokenSupplier
import org.glassfish.hk2.api.Factory
import javax.inject.Inject

class ClientCredentialsTokenSupplierFactory @Inject constructor(applicationParameters: ApplicationParameters, objectMapper: ObjectMapper) : Factory<ClientCredentialsTokenSupplier> {

    private val clientCredentialsTokenSupplier = ClientCredentialsTokenSupplier(
            ClientCredentialsRequest(applicationParameters.clientId, applicationParameters.clientSecret),
            applicationParameters.authServiceBaseUrl,
            objectMapper
    )

    override fun provide() = clientCredentialsTokenSupplier

    override fun dispose(instance: ClientCredentialsTokenSupplier?) {
    }

}