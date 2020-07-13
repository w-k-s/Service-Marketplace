package com.wks.servicesmarketplace.jobservice.adapters.graphql

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component

@Component
class DataFetchingEnvironmentMapper(private val objectMapper: ObjectMapper) {


    @Throws(JsonProcessingException::class)
    fun <T> getArgument(env: DataFetchingEnvironment, argumentName: String?, clazz: TypeReference<T>?): T {
        val json = objectMapper.writeValueAsString(env.getArgument<T>(argumentName))
        return objectMapper.readValue(json, clazz)
    }

    @Throws(JsonProcessingException::class)
    fun <T> getArgument(env: DataFetchingEnvironment, argumentName: String?, clazz: Class<T>?): T {
        val json = objectMapper.writeValueAsString(env.getArgument<T>(argumentName))
        return objectMapper.readValue(json, clazz)
    }
}