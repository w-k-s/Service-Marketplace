package com.wks.servicemarketplace.accountservice.config;

import com.fasterxml.jackson.annotation.JsonFormat;
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
        objectMapper.configOverride(BigDecimal.class).setFormat(JsonFormat.Value.forShape(JsonFormat.Shape.STRING));
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public ObjectMapper provide() {
        return objectMapper;
    }

    @Override
    public void dispose(ObjectMapper t) {
    }
}
