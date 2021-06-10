package com.wks.servicemarketplace.serviceproviderservice.core

import com.fasterxml.jackson.databind.ObjectMapper
import com.wks.servicemarketplace.api.ServiceProviderCreationFailedEvent
import com.wks.servicemarketplace.api.ServiceProviderMessaging
import com.wks.servicemarketplace.common.CompanyRepresentativeUUID
import com.wks.servicemarketplace.common.auth.Authentication
import com.wks.servicemarketplace.common.auth.Permission
import com.wks.servicemarketplace.common.errors.CoreException
import com.wks.servicemarketplace.common.errors.ErrorType
import com.wks.servicemarketplace.common.events.EventEnvelope
import com.wks.servicemarketplace.common.messaging.Message
import com.wks.servicemarketplace.common.messaging.MessageId
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class EmployeeService(
        private val companyRepresentativeDao: CompanyRepresentativeDao,
        private val eventDao: EventDao,
        private val outboxDao: OutboxDao,
        private val objectMapper: ObjectMapper
) {
    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(EmployeeService::class.java)
    }

    fun createCompanyRepresentative(input: CreateCompanyRepresentativeRequest, authentication: Authentication, correlationId: String? = null): CreateCompanyRepresentativeResponse {
        companyRepresentativeDao.connection().use { conn ->
            conn.autoCommit = false

            try {
                authentication.checkRole(Permission.CREATE_COMPANY_REPRESENTATIVE)

                val (companyRepresentative, event) = CompanyRepresentative.create(
                        companyRepresentativeDao.newCompanyRepresentativeId(conn),
                        CompanyRepresentativeUUID(input.uuid),
                        input.name,
                        input.email,
                        input.phoneNumber,
                        authentication.name
                )

                companyRepresentativeDao.save(conn, companyRepresentative)

                val payload = objectMapper.writeValueAsString(event.first())
                eventDao.saveEvent(
                        conn,
                        EventEnvelope(
                                event.first(),
                                companyRepresentative.uuid.toString(),
                                payload
                        )
                )

                outboxDao.saveMessage(
                        conn,
                        Message.builder(
                                MessageId.random(),
                                event.first().toString(),
                                payload,
                                ServiceProviderMessaging.Exchange.MAIN.exchangeName
                        ).withCorrelationId(correlationId)
                                .withDestinationRoutingKey(ServiceProviderMessaging.RoutingKey.SERVICE_PROVIDER_PROFILE_CREATED.value)
                                .build()
                )

                conn.commit()

                return companyRepresentative.let { rep ->
                    CreateCompanyRepresentativeResponse(
                            rep.id,
                            rep.uuid
                    )
                }
            } catch (e: Exception) {
                LOGGER.error("Failed to create company representative")
                handleCompanyRepresentativeCreationFailed(e, correlationId)
                conn.rollback()
                throw e
            }
        }
    }

    private fun handleCompanyRepresentativeCreationFailed(e: Exception, correlationId: String? = null) {
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
                    conn,
                    Message.builder(
                            MessageId.random(),
                            event.eventType.toString(),
                            payload,
                            ServiceProviderMessaging.Exchange.MAIN.exchangeName
                    ).withCorrelationId(correlationId)
                            .withDestinationRoutingKey(ServiceProviderMessaging.RoutingKey.SERVICE_PROVIDER_PROFILE_CREATION_FAILED.value)
                            .build()
            )
        }
    }
}
