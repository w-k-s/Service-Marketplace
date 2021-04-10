package com.wks.servicemarketplace.serviceproviderservice.core.usecase

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder
import com.wks.servicemarketplace.common.*
import com.wks.servicemarketplace.common.auth.Authentication
import com.wks.servicemarketplace.common.auth.Permission
import com.wks.servicemarketplace.common.errors.CoreException
import com.wks.servicemarketplace.common.errors.ErrorType
import com.wks.servicemarketplace.common.events.EventEnvelope
import com.wks.servicemarketplace.common.messaging.Message
import com.wks.servicemarketplace.common.messaging.MessageId
import com.wks.servicemarketplace.api.ServiceProviderCreationFailedEvent
import com.wks.servicemarketplace.api.ServiceProviderMessaging
import com.wks.servicemarketplace.serviceproviderservice.core.CompanyRepresentative
import com.wks.servicemarketplace.serviceproviderservice.core.CompanyRepresentativeDao
import com.wks.servicemarketplace.serviceproviderservice.core.EventDao
import com.wks.servicemarketplace.serviceproviderservice.core.OutboxDao
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import javax.validation.constraints.NotNull

class CreateCompanyRepresentativeUseCase constructor(
    private val companyRepresentativeDao: CompanyRepresentativeDao,
    private val eventDao: EventDao,
    private val outboxDao: OutboxDao,
    private val objectMapper: ObjectMapper
) :
    UseCase<CreateCompanyRepresentativeRequest, CreateCompanyRepresentativeResponse> {

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(CreateCompanyRepresentativeUseCase::class.java)
    }

    override fun execute(input: CreateCompanyRepresentativeRequest): CreateCompanyRepresentativeResponse {
        companyRepresentativeDao.connection().use { conn ->
            conn.autoCommit = false

            try {
                input.authentication.checkRole(Permission.CREATE_COMPANY_REPRESENTATIVE)

                val (companyRepresentative, event) = CompanyRepresentative.create(
                    companyRepresentativeDao.newCompanyRepresentativeId(conn),
                    CompanyRepresentativeUUID(input.uuid),
                    input.name,
                    input.email,
                    input.phoneNumber,
                    input.authentication.name
                )

                companyRepresentativeDao.save(conn, companyRepresentative)

                val payload = objectMapper.writeValueAsString(event.first())
                eventDao.saveEvent(
                    conn, EventEnvelope(
                        event.first(),
                        companyRepresentative.uuid.toString(),
                        payload
                    )
                )

                outboxDao.saveMessage(
                    conn, Message.fromEvent(
                        event.first(),
                        payload,
                        input.correlationId,
                        ServiceProviderMessaging.Exchange.MAIN.exchangeName,
                        ServiceProviderMessaging.RoutingKey.SERVICE_PROVIDER_PROFILE_CREATED.value
                    )
                )

                conn.commit()

                return companyRepresentative.let { rep ->
                    CreateCompanyRepresentativeResponse(
                        rep.externalId,
                        rep.uuid
                    )
                }
            } catch (e: Exception) {
                LOGGER.error("Failed to create company representative")
                handleCompanyRepresentativeCreationFailed(e, input)
                conn.rollback()
                throw e
            }
        }
    }

    private fun handleCompanyRepresentativeCreationFailed(e: Exception, input: CreateCompanyRepresentativeRequest) {
        outboxDao.connection().use { conn ->

            val event = ServiceProviderCreationFailedEvent(
                when (e) {
                    is CoreException -> e.errorType
                    else -> ErrorType.UNKNOWN
                },
                e.message
            )
            val payload = objectMapper.writeValueAsString(event)

            outboxDao.saveMessage(
                conn, Message(
                    MessageId.random(),
                    event.eventType.toString(),
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

@JsonDeserialize(builder = CreateCompanyRepresentativeRequest.Builder::class)
data class CreateCompanyRepresentativeRequest private constructor(
    val uuid: UUID,
    val name: Name,
    val email: Email,
    val phoneNumber: PhoneNumber,
    val authentication: Authentication,
    val correlationId: String?
) {
    @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "")
    class Builder(
        @NotNull
        var uuid: String?,
        @NotNull
        var firstName: String?,
        @NotNull
        var lastName: String?,
        @NotNull
        var email: String?,
        @NotNull
        var mobileNumber: String?,
        @NotNull
        var authentication: Authentication?,
        var correlationId: String?
    ) {
        fun build(): CreateCompanyRepresentativeRequest {
            return ModelValidator.validate(this).let {
                CreateCompanyRepresentativeRequest(
                    UUID.fromString(this.uuid!!),
                    Name.of(this.firstName!!, this.lastName!!),
                    Email.of(this.email!!),
                    PhoneNumber.of(this.mobileNumber!!),
                    this.authentication!!,
                    this.correlationId
                )
            }
        }
    }
}

data class CreateCompanyRepresentativeResponse(
    private val externalId: CompanyRepresentativeId,
    private val uuid: CompanyRepresentativeUUID
)