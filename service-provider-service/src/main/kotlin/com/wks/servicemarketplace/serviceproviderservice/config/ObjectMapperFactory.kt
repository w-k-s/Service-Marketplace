package com.wks.servicemarketplace.serviceproviderservice.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.glassfish.hk2.api.Factory

class ObjectMapperFactory : Factory<ObjectMapper> {

    private val objectMapper: ObjectMapper = ObjectMapper()
            .registerModule(KotlinModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    override fun provide() = objectMapper

    override fun dispose(instance: ObjectMapper?) {

    }
}