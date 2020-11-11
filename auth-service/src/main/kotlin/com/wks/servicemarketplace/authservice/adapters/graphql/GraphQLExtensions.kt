package com.wks.servicemarketplace.authservice.adapters.graphql

import com.fasterxml.jackson.databind.ObjectMapper
import graphql.schema.DataFetchingEnvironment

fun <T> DataFetchingEnvironment.getArgument(key: String, type: Class<T>, objectMapper: ObjectMapper): T {
    val data = this.getArgument<Any>(key)
    val json = objectMapper.writeValueAsBytes(data)
    return objectMapper.readValue(json, type)
}