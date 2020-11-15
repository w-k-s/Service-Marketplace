package com.wks.servicemarketplace.serviceproviderservice.config

import com.fasterxml.jackson.databind.ObjectMapper
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
        }
    }
}