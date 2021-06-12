package com.wks.servicemarketplace.serviceproviderservice.adapters.web.resources

import com.wks.servicemarketplace.api.InternalServiceProviderApi
import com.wks.servicemarketplace.common.UserId
import com.wks.servicemarketplace.common.errors.CoreException
import com.wks.servicemarketplace.common.errors.ErrorType
import com.wks.servicemarketplace.serviceproviderservice.core.CompanyService
import com.wks.servicemarketplace.serviceproviderservice.core.toCompanyProtocolBuffer
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

@Location(InternalServiceProviderApi.PROTO_PATH_V1_)
class InternalServiceProvidersEndpoint {

    @Location(InternalServiceProviderApi.ENDPOINT_GET_COMPANY)
    class Company(val parent: InternalServiceProvidersEndpoint)
}

fun Route.internalCompanyRouting() {
        val companyService by inject<CompanyService>()
        get<InternalServiceProvidersEndpoint.Company> {
            val userUUID = call.request.queryParameters["user_uuid"]?.let { UserId.fromString(it) }
                    ?: throw CoreException(ErrorType.VALIDATION, "User id not provided")
            val company = companyService.findCompanyByEmployeeId(userUUID)
                    ?: throw CoreException(ErrorType.RESOURCE_NOT_FOUND, "No company found for employee with id $userUUID")
            call.respondBytes(
                    bytes = company.toCompanyProtocolBuffer().toByteArray(),
                    contentType = ContentType.Application.ProtoBuf.withoutParameters(),
                    status = HttpStatusCode.OK
            )
        }
}
