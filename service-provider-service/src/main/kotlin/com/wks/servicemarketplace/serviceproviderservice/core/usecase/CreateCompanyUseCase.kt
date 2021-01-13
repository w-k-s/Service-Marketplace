package com.wks.servicemarketplace.serviceproviderservice.core.usecase

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder
import com.wks.servicemarketplace.common.Email
import com.wks.servicemarketplace.common.ModelValidator
import com.wks.servicemarketplace.common.PhoneNumber
import com.wks.servicemarketplace.common.auth.Authentication
import com.wks.servicemarketplace.common.auth.Permission
import com.wks.servicemarketplace.common.errors.CoreException
import com.wks.servicemarketplace.common.errors.ErrorType
import com.wks.servicemarketplace.serviceproviderservice.core.*
import com.wks.servicemarketplace.serviceproviderservice.core.events.EventPublisher
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

class CreateCompanyUseCase @Inject constructor(
    private val companyDao: CompanyDao,
    private val companyRepresentativeDao: CompanyRepresentativeDao,
    private val employeeDao: EmployeeDao,
    private val eventsPublisher: EventPublisher
) : UseCase<CreateCompanyRequest, CreateCompanyResponse> {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(CreateCompanyUseCase::class.java)
    }

    override fun execute(input: CreateCompanyRequest): CreateCompanyResponse {
        companyDao.connection().use {
            try {
                it.autoCommit = false

                input.authentication.checkRole(Permission.CREATE_COMPANY)

                val uuid = input.authentication.userId?.let { userId -> CompanyRepresentativeUUID.of(userId) }
                    ?: throw CoreException(ErrorType.AUTHORIZATION, "Company representative id not found in token")

                val companyRepresentative = companyRepresentativeDao.findByUUID(it, uuid)
                    ?: throw CoreException(
                        ErrorType.RESOURCE_NOT_FOUND,
                        "Company Representative '$uuid' not found",
                        details = mapOf("uuid" to uuid.toString())
                    )

                val (company, events) = Company.create(
                    companyDao.newCompanyId(it),
                    input.name,
                    input.email,
                    input.phone,
                    input.services,
                    companyRepresentative.uuid
                ).also { (company, _) ->
                    companyDao.save(it, company)
                }

                companyRepresentative.toEmployee(company.externalId, input.authentication.name)
                    .also { admin -> employeeDao.save(it, admin) }

                companyDao.setAdministrator(it, company, companyRepresentative)
                companyRepresentativeDao.delete(it, companyRepresentative.externalId)

                it.commit()

                eventsPublisher.companyCreated(
                    input.authentication.token,
                    events.first { event -> event is CompanyCreatedEvent } as CompanyCreatedEvent)

                return CreateCompanyResponse(
                    company.externalId,
                    company.uuid,
                    company.name,
                    company.phone,
                    company.email,
                    company.createdBy,
                    company.createdDate,
                    company.lastModifiedBy,
                    company.lastModifiedDate,
                    company.version
                )

            } catch (e: Exception) {
                LOGGER.error("Failed to create company '${e.message}'", e)
                it.rollback()
                throw e
            }
        }
    }
}

@JsonDeserialize(builder = CreateCompanyRequest.Builder::class)
data class CreateCompanyRequest(
    val name: String,
    val phone: PhoneNumber,
    val email: Email,
    val logoUrl: String?,
    val services: Services,
    val authentication: Authentication
) {
    @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "")
    class Builder(
        @NotNull
        var uuid: String?,
        @NotNull
        var name: String?,
        @NotNull
        var email: String?,
        @NotNull
        var phoneNumber: String?,
        var logoUrl: String?,
        @NotNull
        @NotEmpty
        var services: List<String>?,
        @NotNull
        var authentication: Authentication?
    ) {

        fun build(): CreateCompanyRequest {
            return ModelValidator.validate(this).let {
                CreateCompanyRequest(
                    this.name!!,
                    PhoneNumber.of(this.phoneNumber!!),
                    Email.of(this.email!!),
                    this.logoUrl,
                    Services.of(this.services!!),
                    this.authentication!!
                )
            }
        }
    }
}

data class CreateCompanyResponse(
    val externalId: CompanyId,
    val uuid: CompanyUUID,
    val name: String,
    val phone: PhoneNumber,
    val email: Email,
    val createdBy: CompanyRepresentativeUUID,
    val createdDate: OffsetDateTime,
    val lastModifiedBy: String? = null,
    val lastModifiedDate: OffsetDateTime? = null,
    val version: Long
)