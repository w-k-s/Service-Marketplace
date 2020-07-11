package com.wks.servicemarketplace.accountservice.adapters.graphql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.schema.DataFetchingEnvironment;

import javax.inject.Inject;
import java.util.Optional;

public class DataFetchingEnvironmentMapper {

    private final ObjectMapper objectMapper;

    @Inject
    public DataFetchingEnvironmentMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T getArgument(DataFetchingEnvironment env, String argumentName, TypeReference<T> clazz) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(env.<T>getArgument(argumentName));
        return objectMapper.readValue(json, clazz);
    }

    public <T> T getArgument(DataFetchingEnvironment env, String argumentName, Class<T> clazz) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(env.<T>getArgument(argumentName));
        return objectMapper.readValue(json, clazz);
    }
}
