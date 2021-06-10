package com.wks.servicemarketplace.serviceproviderservice.core

import com.fasterxml.jackson.databind.ObjectMapper
import com.wks.servicemarketplace.api.ServiceProviderMessaging
import com.wks.servicemarketplace.common.CompanyRepresentativeUUID
import com.wks.servicemarketplace.common.UserId
import com.wks.servicemarketplace.common.auth.Authentication
import com.wks.servicemarketplace.common.auth.Permission
import com.wks.servicemarketplace.common.errors.CoreException
import com.wks.servicemarketplace.common.errors.ErrorType
import com.wks.servicemarketplace.common.events.DefaultFailureEvent
import com.wks.servicemarketplace.common.events.EventEnvelope
import com.wks.servicemarketplace.common.events.EventType
import com.wks.servicemarketplace.common.messaging.Message
import com.wks.servicemarketplace.common.messaging.MessageId
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class CompanyService(
        private val companyDao: CompanyDao,
        private val companyRepresentativeDao: CompanyRepresentativeDao,
        private val employeeDao: EmployeeDao,
        private val eventDao: EventDao,
        private val outboxDao: OutboxDao,
        private val objectMapper: ObjectMapper
) {
    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(CompanyService::class.java)
    }

    fun createCompany(input: CreateCompanyRequest, authentication: Authentication): CreateCompanyResponse {
        companyDao.connection().use {
            try {
                it.autoCommit = false

                authentication.checkRole(Permission.CREATE_COMPANY)

                val uuid = authentication.userId?.let { userId -> CompanyRepresentativeUUID.of(userId) }
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

                companyRepresentative.toEmployee(company.id, authentication.name)
                        .also { admin -> employeeDao.save(it, admin) }

                companyDao.setAdministrator(it, company, companyRepresentative)
                companyRepresentativeDao.delete(it, companyRepresentative.id)

                val payload = objectMapper.writeValueAsString(events.first())
                eventDao.saveEvent(
                        it,
                        EventEnvelope(
                                events.first(),
                                company.uuid.toString(),
                                payload
                        )
                )
                outboxDao.saveMessage(
                        it,
                        Message.builder(
                                MessageId.random(),
                                events.first().eventType.toString(),
                                payload,
                                ServiceProviderMessaging.Exchange.MAIN.exchangeName
                        ).withCorrelationId(input.correlationId)
                                .withDestinationRoutingKey(ServiceProviderMessaging.RoutingKey.COMPANY_CREATED.value)
                                .build()
                )

                it.commit()

                return CreateCompanyResponse(
                        company.id,
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
                    conn,
                    Message.builder(
                            MessageId.random(),
                            EventType.SERVICE_PROVIDER_PROFILE_CREATION_FAILED.toString(),
                            payload,
                            ServiceProviderMessaging.Exchange.MAIN.exchangeName
                    ).withCorrelationId(input.correlationId)
                            .withDestinationRoutingKey(ServiceProviderMessaging.RoutingKey.SERVICE_PROVIDER_PROFILE_CREATION_FAILED.value)
                            .build()
            )
        }
    }

    fun findCompanyByEmployeeId(userUUID: UserId) = companyDao.findByEmployeeId(companyDao.connection(), userUUID)
}