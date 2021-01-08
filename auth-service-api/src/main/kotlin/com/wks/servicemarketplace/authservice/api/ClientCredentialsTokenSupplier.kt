package com.wks.servicemarketplace.authservice.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.wks.servicemarketplace.common.auth.Token
import com.wks.servicemarketplace.common.auth.isExpired
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicReference
import java.util.function.Supplier


class ClientCredentialsTokenSupplier constructor(
        private val clientCredentials: ClientCredentialsRequest,
        authServiceBaseUrl: String,
        objectMapper: ObjectMapper) : Supplier<CompletableFuture<Token>> {

    companion object {
        val TOKEN: AtomicReference<Token?> = AtomicReference()
    }

    private val authService: AuthService = Retrofit.Builder()
            .baseUrl(authServiceBaseUrl)
            .addConverterFactory(JacksonConverterFactory.create(objectMapper))
            .build()
            .create(AuthService::class.java)

    override fun get(): CompletableFuture<Token> {
        TOKEN.get()?.let {
            if (!it.isExpired()) {
                return CompletableFuture.completedFuture(it)
            }
        }
        return getRequestTokenFuture()
    }

    private fun getRequestTokenFuture(): CompletableFuture<Token> {
        return CompletableFuture.supplyAsync {
            synchronized(TOKEN) {
                if (TOKEN.get() == null) {
                    val response = authService.clientCredentials(clientCredentials).execute()
                    response.body()?.let {
                        TOKEN.compareAndSet(null, it)
                    }
                }
                TOKEN.get()
            }
        }
    }
}