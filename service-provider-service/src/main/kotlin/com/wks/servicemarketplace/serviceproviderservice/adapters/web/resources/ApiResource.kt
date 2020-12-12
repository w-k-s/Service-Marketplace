package com.wks.servicemarketplace.serviceproviderservice.adapters.web.resources

import com.wks.servicemarketplace.serviceproviderservice.core.auth.Authentication
import com.wks.servicemarketplace.serviceproviderservice.core.usecase.CreateCompanyRequest
import com.wks.servicemarketplace.serviceproviderservice.core.usecase.CreateCompanyResponse
import com.wks.servicemarketplace.serviceproviderservice.core.usecase.CreateCompanyUseCase
import javax.annotation.security.RolesAllowed
import javax.inject.Inject
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.SecurityContext

@Path("/api/v1/serviceproviders")
class ApiResource @Inject constructor(private val createCompanyUseCase: CreateCompanyUseCase) {

    @POST
    @Path("/company")
    @RolesAllowed("company.create")
    @Produces(MediaType.APPLICATION_JSON)
    fun createCompany(request: CreateCompanyRequest.Builder, @Context context: SecurityContext) : CreateCompanyResponse {
        return createCompanyUseCase.execute(request
                .authentication(context.userPrincipal as? Authentication)
                .build()
        )
    }
}