package com.wks.servicemarketplace.authservice.adapters.web.resources

import com.wks.servicemarketplace.authservice.core.dtos.ClientCredentialsRequest
import com.wks.servicemarketplace.authservice.core.dtos.RegisterRequest
import com.wks.servicemarketplace.authservice.core.dtos.SignInRequest
import com.wks.servicemarketplace.authservice.core.iam.TokenService
import javax.inject.Inject
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/api/v1")
class ApiResource @Inject constructor(private val tokenService: TokenService) {

    @POST
    @Path("/user")
    @Produces(MediaType.APPLICATION_JSON)
    fun register(request: RegisterRequest.Builder) = tokenService.register(request.build())

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    fun login(request: SignInRequest.Builder) = tokenService.login(request.build())

    @POST
    @Path("/token")
    @Produces(MediaType.APPLICATION_JSON)
    fun token(request: ClientCredentialsRequest.Builder) = tokenService.apiToken(request.build())
}