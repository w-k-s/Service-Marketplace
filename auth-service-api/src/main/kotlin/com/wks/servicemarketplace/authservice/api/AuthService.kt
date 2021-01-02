package com.wks.servicemarketplace.authservice.api

import com.sun.el.parser.Token
import com.wks.servicemarketplace.common.auth.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    companion object {
        const val PATH_V1_ = "/api/v1/auth"
        const val ENDPOINT_LOGIN = "/login"
        const val ENDPOINT_REGISTER = "/user"
        const val ENDPOINT_CLIENT_CREDENTIALS = "/token"
    }

    @POST("$PATH_V1_$ENDPOINT_LOGIN")
    fun login(@Body request: Credentials): Call<Token>

    @POST("$PATH_V1_$ENDPOINT_REGISTER")
    fun register(@Body request: Registration): Call<User>

    @POST("$PATH_V1_$ENDPOINT_CLIENT_CREDENTIALS")
    fun clientCredentials(@Body request: Registration): Call<Token>
}