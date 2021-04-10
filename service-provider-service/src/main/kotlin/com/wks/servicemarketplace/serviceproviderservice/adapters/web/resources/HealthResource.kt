package com.wks.servicemarketplace.serviceproviderservice.adapters.web.resources

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

@Location("/health")
class HealthCheckEndpoint

fun Route.healthCheckRouting() {

    get<HealthCheckEndpoint> {
        call.respond(status = HttpStatusCode.OK, message = "")
    }
}