package com.wks.servicemarketplace.authservice.adapters.web.resources

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.core.Response

@Path("/")
class HealthResource {

    @GET
    @Path("/health")
    fun healthCheck(): Response {
        return Response.ok().build()
    }
}