package com.wks.servicemarketplace.serviceproviderservice.adapters.web.resources

import com.wks.servicemarketplace.serviceproviderservice.DefaultPrincipal
import com.wks.servicemarketplace.serviceproviderservice.core.usecase.CreateCompanyRequest
import com.wks.servicemarketplace.serviceproviderservice.core.usecase.CreateCompanyUseCase
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

@Location("/api/v1/serviceproviders")
class ServiceProvidersEndpoint {

    @Location("/company")
    class Company(val parent: ServiceProvidersEndpoint)
}

fun Route.serviceProviderRouting() {

    authenticate("standardJwtToken") {
        val createCompanyUseCase by inject<CreateCompanyUseCase>()
        post<ServiceProvidersEndpoint.Company> {
            val request = call.receive<CreateCompanyRequest.Builder>()
            call.respond(createCompanyUseCase.execute(request.also {
                it.authentication = (call.authentication.principal as DefaultPrincipal).value
            }.build()))
        }
    }
}
