package com.wks.servicemarketplace.serviceproviderservice.adapters.web.resources

import com.wks.servicemarketplace.api.ServiceProviderApi
import com.wks.servicemarketplace.serviceproviderservice.DefaultPrincipal
import com.wks.servicemarketplace.serviceproviderservice.core.CompanyService
import com.wks.servicemarketplace.serviceproviderservice.core.CreateCompanyRequest
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

@Location(ServiceProviderApi.API_PATH_V1_)
class ServiceProvidersEndpoint {

    @Location(ServiceProviderApi.ENDPOINT_CREATE_COMPANY)
    class Company(val parent: ServiceProvidersEndpoint)
}

fun Route.companyRouting() {

    authenticate("standardJwtToken") {
        val companyService by inject<CompanyService>()

        post<ServiceProvidersEndpoint.Company> {
            val request = call.receive<CreateCompanyRequest>()
            val authentication = (call.authentication.principal as DefaultPrincipal).value
            val response = companyService.createCompany(request, authentication)
            call.respond(response)
        }
    }
}
