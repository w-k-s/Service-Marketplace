package com.wks.servicemarketplace.authservice.config

import org.glassfish.hk2.api.Factory
import javax.inject.Inject


class FusionAuthConfigurationFactory @Inject constructor(private val applicationParameters: ApplicationParameters) : Factory<FusionAuthConfiguration> {
    override fun provide() = applicationParameters.fusionAuthConfiguration

    override fun dispose(instance: FusionAuthConfiguration?) {
    }
}