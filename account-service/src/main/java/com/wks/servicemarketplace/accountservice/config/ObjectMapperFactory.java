package com.wks.servicemarketplace.accountservice.config;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.glassfish.hk2.api.Factory;

import java.math.BigDecimal;

public class ObjectMapperFactory implements Factory<ObjectMapper> {

    final ObjectMapper objectMapper;

    public ObjectMapperFactory() {
        this.objectMapper = new ObjectMapper();
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
        objectMapper.configOverride(BigDecimal.class).setFormat(JsonFormat.Value.forShape(JsonFormat.Shape.STRING));
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public ObjectMapper provide() {
        return objectMapper;
    }

    @Override
    public void dispose(ObjectMapper t) {
    }
}
