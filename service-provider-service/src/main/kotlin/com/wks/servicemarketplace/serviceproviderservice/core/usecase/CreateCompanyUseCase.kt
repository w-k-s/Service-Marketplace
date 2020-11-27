package com.wks.servicemarketplace.serviceproviderservice.core.usecase

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder
import com.wks.servicemarketplace.serviceproviderservice.core.*
import com.wks.servicemarketplace.serviceproviderservice.core.auth.Authentication
import com.wks.servicemarketplace.serviceproviderservice.core.events.EventPublisher
import com.wks.servicemarketplace.serviceproviderservice.core.exceptions.CoreRuntimeException
import com.wks.servicemarketplace.serviceproviderservice.core.exceptions.ErrorType
import com.wks.servicemarketplace.serviceproviderservice.core.exceptions.UnauthorizedException
import com.wks.servicemarketplace.serviceproviderservice.core.utils.ModelValidator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.OffsetDateTime
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

class CreateCompanyUseCase(private val companyDao: CompanyDao,
                           private val companyRepresentativeDao: CompanyRepresentativeDao,
                           private val employeeDao: EmployeeDao,
                           private val eventsPublisher: EventPublisher) : UseCase<CreateCompanyRequest, CreateCompanyResponse> {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(CreateCompanyUseCase::class.java)
    }

    override fun execute(input: CreateCompanyRequest): CreateCompanyResponse {
        companyDao.connection().use {
            try {
                it.autoCommit = false

                input.authentication.checkRole("company.create")

                val uuid = input.authentication.user?.id?.let { uuid -> CompanyRepresentativeUUID.fromString(uuid) }
                        ?: throw UnauthorizedException("Company representative id not found in token")

                val companyRepresentative = companyRepresentativeDao.findByUUID(it, uuid)
                        ?: throw CoreRuntimeException(ErrorType.NOT_FOUND, "Company Representative '$uuid' not found", mapOf("uuid" to listOf(uuid.toString())))

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

                eventsPublisher.companyCreated(input.authentication.token, events.first { event -> event is CompanyCreatedEvent } as CompanyCreatedEvent)

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

            } catch (e: CoreRuntimeException) {
                LOGGER.error("Failed to create company '${e.message}'", e)
                it.rollback()
                throw e
            } catch (e: Exception) {
                LOGGER.error("Failed to create company '${e.message}'", e)
                it.rollback()
                throw CoreRuntimeException(ErrorType.UNKNOWN, e)
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
        fun name(name: String?): Builder {
            this.name = name
            return this
        }

        fun email(email: String?): Builder {
            this.email = email
            return this
        }

        fun logoUrl(logoUrl: String?): Builder {
            this.logoUrl = logoUrl
            return this
        }

        fun phoneNumber(phoneNumber: String?): Builder {
            this.phoneNumber = phoneNumber
            return this
        }

        fun authentication(authentication: Authentication?): Builder {
            this.authentication = authentication
            return this
        }

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