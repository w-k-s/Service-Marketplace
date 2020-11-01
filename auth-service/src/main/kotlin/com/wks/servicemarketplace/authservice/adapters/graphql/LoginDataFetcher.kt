package com.wks.servicemarketplace.authservice.adapters.graphql

import com.wks.servicemarketplace.authservice.core.Token
import com.wks.servicemarketplace.authservice.core.dtos.SignInRequest
import com.wks.servicemarketplace.authservice.core.iam.TokenService
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import javax.inject.Inject

class LoginDataFetcher @Inject constructor(private val tokenService: TokenService) : DataFetcher<Token> {
    override fun get(environment: DataFetchingEnvironment): Token {
        val data = environment.getArgument<Map<String, String>>("data")

        return tokenService.login(SignInRequest.Builder()
                .username(data["username"])
                .password(data["password"])
                .build())
    }
}