package com.wks.servicemarketplace.authservice.adapters.web.resources

import com.wks.servicemarketplace.authservice.core.IAMAdapter
import javax.inject.Inject
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/")
class AuthResource @Inject constructor(private val iam: IAMAdapter) {

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun login(loginRequest: LoginRequest): Response {
        val token = iam.login(loginRequest)
        return Response.ok(token).build()
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun login(registrationRequest: RegistrationRequest): Response {
        val identity = iam.register(registrationRequest)
        return Response.ok(identity).build()
    }
}