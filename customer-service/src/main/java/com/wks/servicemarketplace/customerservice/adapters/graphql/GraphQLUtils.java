package com.wks.servicemarketplace.customerservice.adapters.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import graphql.schema.DataFetchingEnvironment;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

public class GraphQLUtils {

    public static <T> Optional<T> getArgument(DataFetchingEnvironment environment, String argument, Class<T> type, ObjectMapper objectMapper) {
        Preconditions.checkNotNull(environment);
        Preconditions.checkNotNull(type);
        Preconditions.checkNotNull(objectMapper);
        Preconditions.checkArgument(StringUtils.isNotBlank(argument));

        try {
            final Object object = environment.getArgument(argument);
            final String json = objectMapper.writeValueAsString(object);
            return Optional.ofNullable(objectMapper.readValue(json, type));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
