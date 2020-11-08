package com.wks.servicemarketplace.customerservice.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

@Provider
@Consumes(MediaType.WILDCARD) // NOTE: required to support "non-standard" JSON variants
@Produces(MediaType.WILDCARD)
public class ObjectMapperProvider extends JacksonJaxbJsonProvider {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.registerModule(new JavaTimeModule());
        //
        // WARNING: Removing this line will cause the service to not be registered with the apollo gateway.
        // The horrible Apollo Gateway library I'm using (https://www.npmjs.com/package/@apollo/gateway, version: 0.17.0
        // considers an error field with an empty list ('[]') to be an error.
        // As a temporary workaround, I'm omitting empty arrays from the json response.
        //
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public ObjectMapperProvider() {
        super(objectMapper, JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS);
    }
}
