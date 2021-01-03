package com.wks.servicemarketplace.authservice.adapters.web.resources

import com.wks.servicemarketplace.authservice.api.AuthService
import com.wks.servicemarketplace.authservice.api.ClientCredentialsRequest
import com.wks.servicemarketplace.authservice.api.RegisterRequest
import com.wks.servicemarketplace.authservice.api.SignInRequest
import com.wks.servicemarketplace.authservice.core.TokenService
import javax.inject.Inject
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path(AuthService.PATH_V1_)
class ApiResource @Inject constructor(private val tokenService: TokenService) {

    @POST
    @Path(AuthService.ENDPOINT_REGISTER)
    @Produces(MediaType.APPLICATION_JSON)
    fun register(request: RegisterRequest.Builder) = tokenService.register(request.build())

    @POST
    @Path(AuthService.ENDPOINT_LOGIN)
    @Produces(MediaType.APPLICATION_JSON)
    fun login(request: SignInRequest.Builder) = tokenService.login(request.build())

    @POST
    @Path(AuthService.ENDPOINT_CLIENT_CREDENTIALS)
    @Produces(MediaType.APPLICATION_JSON)
    fun token(request: ClientCredentialsRequest.Builder) = tokenService.apiToken(request.build())
}