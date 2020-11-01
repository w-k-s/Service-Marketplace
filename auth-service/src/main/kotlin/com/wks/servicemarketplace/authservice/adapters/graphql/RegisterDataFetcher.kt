package com.wks.servicemarketplace.authservice.adapters.graphql

import com.wks.servicemarketplace.authservice.core.Identity
import com.wks.servicemarketplace.authservice.core.UserType
import com.wks.servicemarketplace.authservice.core.dtos.RegisterRequest
import com.wks.servicemarketplace.authservice.core.iam.TokenService
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import javax.inject.Inject

class RegisterDataFetcher @Inject constructor(private val tokenService: TokenService) : DataFetcher<Identity> {
    override fun get(environment: DataFetchingEnvironment): Identity {
        val data = environment.getArgument<Map<String, String>>("data")

        return tokenService.register(
            RegisterRequest.Builder()
                .firstName(data["firstName"])
                .lastName(data["lastName"])
                .email(data["email"])
                .password(data["password"])
                .userType(UserType.valueOf(data["userType"] ?: ""))
                .build()
        )
    }
}