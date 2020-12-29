package com.wks.servicemarketplace.authservice.config

import com.wks.servicemarketplace.authservice.core.Token
import com.wks.servicemarketplace.authservice.core.dtos.ClientCredentialsRequest
import com.wks.servicemarketplace.authservice.core.iam.TokenService
import com.wks.servicemarketplace.authservice.core.isExpired
import org.glassfish.hk2.api.Factory
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier
import javax.inject.Inject

class ClientCredentialsTokenSupplier @Inject constructor(private val applicationParameters: ApplicationParameters,
                                                         private val tokenService: TokenService) : Supplier<CompletableFuture<Token>> {

    private var token: Token? = null

    override fun get(): CompletableFuture<Token> {
        if (token?.isExpired() == true) return getRequestTokenFuture()
        return CompletableFuture.completedFuture(token)
    }

    private fun getRequestTokenFuture(): CompletableFuture<Token> {
        return CompletableFuture.supplyAsync {
            tokenService.apiToken(ClientCredentialsRequest(
                    applicationParameters.clientId,
                    applicationParameters.clientSecret
            ))
        }.thenApply { this.token = it; it }
    }
}

class ClientCredentialsTokenSupplierFactory @Inject constructor(applicationParameters: ApplicationParameters,
                                                                tokenService: TokenService) : Factory<ClientCredentialsTokenSupplier> {

    private val clientCredentialsTokenSupplier = ClientCredentialsTokenSupplier(applicationParameters, tokenService)

    override fun provide() = clientCredentialsTokenSupplier

    override fun dispose(instance: ClientCredentialsTokenSupplier?) {
    }

}