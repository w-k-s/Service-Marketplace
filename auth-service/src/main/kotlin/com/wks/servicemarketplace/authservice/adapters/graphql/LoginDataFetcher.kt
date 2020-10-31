package com.wks.servicemarketplace.authservice.adapters.graphql

import com.wks.servicemarketplace.authservice.core.IAMAdapter
import com.wks.servicemarketplace.authservice.core.Token
import com.wks.servicemarketplace.authservice.core.dtos.SignInRequest
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import javax.inject.Inject

class LoginDataFetcher @Inject constructor(private val iam: IAMAdapter) : DataFetcher<Token> {
    override fun get(environment: DataFetchingEnvironment): Token {
        val data = environment.getArgument<Map<String, String>>("data")

        return iam.login(SignInRequest.Builder()
                .username(data["username"])
                .password(data["password"])
                .build())
    }
}