package com.wks.servicesmarketplace.jobservice.adapters.graphql

import com.fasterxml.jackson.annotation.JsonInclude
import graphql.ExecutionResult
import graphql.GraphQLError

/**
 * Apollo-server considers errors: [] to be an error.
 * This causes the service to not be detected.
 * As a workaround, ExecutionResult from GraphQL is wrapped in ApolloExecutionResult.
 * ApolloExecutionResult returns null when the errors array is empty.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApolloExecutionResult(private val executionResult: ExecutionResult?) : ExecutionResult {

    override fun toSpecification(): MutableMap<String, Any>? {
        if (this.executionResult?.toSpecification()?.isNotEmpty() == true) {
            return this.executionResult.toSpecification()
        }
        return null
    }

    override fun getErrors(): MutableList<GraphQLError>? {
        if (this.executionResult?.errors?.isNotEmpty() == true) {
            return this.executionResult.errors
        }
        return null
    }

    override fun isDataPresent(): Boolean {
        return this.executionResult?.isDataPresent == true
    }

    override fun getExtensions(): MutableMap<Any, Any>? {
        if (this.executionResult?.extensions?.isNotEmpty() == true) {
            return this.executionResult.extensions
        }
        return null
    }

    override fun <T : Any?> getData(): T? {
        return this.executionResult?.getData()
    }
}