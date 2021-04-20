package com.wks.servicemarketplace.authservice.adapters.web.resources

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("")
class HealthResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/health")
    fun healthCheck(): Response {
        return Response.ok().build()
    }
}