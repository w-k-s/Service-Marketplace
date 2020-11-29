package com.wks.servicemarketplace.serviceproviderservice.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider
import com.fasterxml.jackson.module.kotlin.KotlinModule
import javax.ws.rs.Consumes
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.ext.Provider

@Provider
@Consumes(MediaType.WILDCARD)
@Produces(MediaType.WILDCARD)
class ObjectMapperProvider : JacksonJaxbJsonProvider(objectMapper, DEFAULT_ANNOTATIONS){

    companion object{
        val objectMapper = ObjectMapper()
        init{
            objectMapper.registerModule(KotlinModule())
                    .registerModule(JavaTimeModule())
                    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        }
    }
}