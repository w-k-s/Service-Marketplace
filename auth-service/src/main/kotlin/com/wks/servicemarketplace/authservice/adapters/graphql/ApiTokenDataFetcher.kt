package com.wks.servicemarketplace.authservice.adapters.graphql

import com.fasterxml.jackson.databind.ObjectMapper
import com.wks.servicemarketplace.authservice.core.Token
import com.wks.servicemarketplace.authservice.core.dtos.ClientCredentialsRequest
import com.wks.servicemarketplace.authservice.core.iam.TokenService
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import javax.inject.Inject

class ApiTokenDataFetcher @Inject constructor(private val tokenService: TokenService,
                                              private val objectMapper: ObjectMapper) : DataFetcher<Token> {
    override fun get(environment: DataFetchingEnvironment): Token {
        val clientCredentialsRequestBuilder = environment.getArgument("data", ClientCredentialsRequest.Builder::class.java, objectMapper)
        return tokenService.apiToken(clientCredentialsRequestBuilder.build())
    }
}