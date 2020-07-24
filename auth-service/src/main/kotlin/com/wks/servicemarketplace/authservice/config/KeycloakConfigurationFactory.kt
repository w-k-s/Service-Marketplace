package com.wks.servicemarketplace.authservice.config

import org.glassfish.hk2.api.Factory
import javax.inject.Inject


class KeycloakConfigurationFactory @Inject constructor(private val applicationParameters: ApplicationParameters) : Factory<KeycloakConfiguration> {
    override fun provide() = applicationParameters.keycloakConfiguration

    override fun dispose(instance: KeycloakConfiguration?) {
    }
}