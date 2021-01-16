package com.wks.servicemarketplace.serviceproviderservice.core.usecase

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder
import com.wks.servicemarketplace.common.*
import com.wks.servicemarketplace.common.auth.Authentication
import com.wks.servicemarketplace.common.auth.Permission
import com.wks.servicemarketplace.common.errors.CoreException
import com.wks.servicemarketplace.common.errors.ErrorType
import com.wks.servicemarketplace.common.events.DefaultFailureEvent
import com.wks.servicemarketplace.common.events.EventEnvelope
import com.wks.servicemarketplace.common.events.EventType
import com.wks.servicemarketplace.common.messaging.Message
import com.wks.servicemarketplace.common.messaging.MessageId
import com.wks.servicemarketplace.messaging.ServiceProviderMessaging
import com.wks.servicemarketplace.serviceproviderservice.core.*
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
    private val eventDao: EventDao,
    private val outboxDao: OutboxDao,
    private val objectMapper: ObjectMapper
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

                val payload = objectMapper.writeValueAsString(events.first())
                eventDao.saveEvent(
                    it, EventEnvelope(
                        events.first(),
                        company.uuid.toString(),
                        payload
                    )
                )
                outboxDao.saveMessage(
                    it, Message(
                        MessageId.random(),
                        events.first().eventType.toString(),
                        payload,
                        ServiceProviderMessaging.Exchange.MAIN.exchangeName,
                        false,
                        input.correlationId,
                        ServiceProviderMessaging.RoutingKey.COMPANY_CREATED.value
                    )
                )

                it.commit()

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
                handleCompanyCreationFailed(e, input)
                throw e
            }
        }
    }

    private fun handleCompanyCreationFailed(e: Exception, input: CreateCompanyRequest) {
        outboxDao.connection().use { conn ->

            val payload = objectMapper.writeValueAsString(
                DefaultFailureEvent(
                    EventType.SERVICE_PROVIDER_PROFILE_CREATION_FAILED,
                    "ServiceProvider",
                    when (e) {
                        is CoreException -> e.errorType
                        else -> ErrorType.UNKNOWN
                    },
                    e.message ?: "Unknown error"
                )
            )
            outboxDao.saveMessage(
                conn, Message(
                    MessageId.random(),
                    EventType.SERVICE_PROVIDER_PROFILE_CREATION_FAILED.toString(),
                    payload,
                    ServiceProviderMessaging.Exchange.MAIN.exchangeName,
                    false,
                    input.correlationId,
                    ServiceProviderMessaging.RoutingKey.SERVICE_PROVIDER_PROFILE_CREATION_FAILED.value
                )
            )
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
    val authentication: Authentication,
    val correlationId: String?
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
        var authentication: Authentication?,
        var correlationId: String?
    ) {

        fun build(): CreateCompanyRequest {
            return ModelValidator.validate(this).let {
                CreateCompanyRequest(
                    this.name!!,
                    PhoneNumber.of(this.phoneNumber!!),
                    Email.of(this.email!!),
                    this.logoUrl,
                    Services.of(this.services!!),
                    this.authentication!!,
                    this.correlationId
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