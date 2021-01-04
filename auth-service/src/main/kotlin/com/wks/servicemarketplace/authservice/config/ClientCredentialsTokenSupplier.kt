package com.wks.servicemarketplace.authservice.config

import com.wks.servicemarketplace.authservice.api.ClientCredentialsRequest
import com.wks.servicemarketplace.authservice.core.TokenService
import com.wks.servicemarketplace.common.auth.Token
import com.wks.servicemarketplace.common.auth.isExpired
import org.glassfish.hk2.api.Factory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicReference
import java.util.function.Supplier
import javax.inject.Inject

class ClientCredentialsTokenSupplier @Inject constructor(private val applicationParameters: ApplicationParameters,
                                                         private val tokenService: TokenService) : Supplier<CompletableFuture<Token>> {

    companion object {
        val TOKEN: AtomicReference<Token?> = AtomicReference()
    }

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
                    val token = tokenService.apiToken(ClientCredentialsRequest(
                            applicationParameters.clientId,
                            applicationParameters.clientSecret
                    ))
                    TOKEN.compareAndSet(null, token)
                }
                TOKEN.get()
            }
        }
    }
}

class ClientCredentialsTokenSupplierFactory @Inject constructor(applicationParameters: ApplicationParameters,
                                                                tokenService: TokenService) : Factory<ClientCredentialsTokenSupplier> {

    private val clientCredentialsTokenSupplier = ClientCredentialsTokenSupplier(applicationParameters, tokenService)

    override fun provide() = clientCredentialsTokenSupplier

    override fun dispose(instance: ClientCredentialsTokenSupplier?) {
    }

}