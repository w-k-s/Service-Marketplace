package com.wks.servicemarketplace.authservice.adapters.auth.fusionauth

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.*
import com.wks.servicemarketplace.authservice.adapters.events.AutoDelete
import com.wks.servicemarketplace.authservice.adapters.events.Durable
import com.wks.servicemarketplace.authservice.adapters.events.Exclusive
import com.wks.servicemarketplace.authservice.config.ApplicationParameters
import com.wks.servicemarketplace.authservice.core.errors.RegistrationFailedException
import io.fusionauth.client.FusionAuthClient
import io.fusionauth.domain.GroupMember
import io.fusionauth.domain.api.MemberRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.*
import javax.inject.Inject

internal data class RetryAssignGroupEvent(val groupId: UUID, val userId: String)

// TODO: This works but I definitely want to be able to integration test this
class AssignGroupRetrier @Inject constructor(private val channel: Channel,
                                             private val config: ApplicationParameters,
                                             private val objectMapper: ObjectMapper) {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(AssignGroupRetrier::class.java)
        private const val EXCHANGE_RETRY_ASSIGN_GROUP = "com.wks.servicemarketplace.auth.exchange.retryAssignGroup"
        private const val ROUTING_KEY_RETRY_ASSIGN_GROUP = "com.wks.servicemarketplace.auth.retryAssignGroup"
        private const val QUEUE_RETRY_ASSIGN_GROUP = "com.wks.servicemarketplace.auth.queue.retryAssignGroup"
        private const val DX_EXCHANGE_RETRY_ASSIGN_GROUP = "com.wks.servicemarketplace.auth.dx.exchange.retryAssignGroup"
        private const val DX_QUEUE_RETRY_ASSIGN_GROUP = "com.wks.servicemarketplace.auth.dx.queue.retryAssignGroup"
    }

    private val fusionAuthClient = config.fusionAuthConfiguration.let { FusionAuthClient(it.apiKey, it.serverUrl, it.tenantId) }

    init {
        declareExchangesAndQueues()
        receiveRetryAssignGroupEvents()
    }

    fun retry(groupId: UUID, userId: String) {
        channel.basicPublish(
                EXCHANGE_RETRY_ASSIGN_GROUP,
                ROUTING_KEY_RETRY_ASSIGN_GROUP,
                MessageProperties.PERSISTENT_TEXT_PLAIN,
                objectMapper.writeValueAsBytes(RetryAssignGroupEvent(groupId, userId))
        )
        LOGGER.info("Published retry event for group: '$groupId', userId: '$userId'")
    }

    private fun receiveRetryAssignGroupEvents() {
        val deliverCallback = DeliverCallback { _: String?, delivery: Delivery ->
            val retryAssignGroupEvent = objectMapper.readValue(delivery.body, RetryAssignGroupEvent::class.java)

            try {
                LOGGER.info("Retrying assign group: $retryAssignGroupEvent")
                val response = fusionAuthClient.createGroupMembers(MemberRequest(
                        retryAssignGroupEvent.groupId,
                        listOf(GroupMember().also {
                            it.userId = UUID.fromString(retryAssignGroupEvent.userId)
                        })
                ))
                if (!response.wasSuccessful()) {
                    throw RegistrationFailedException("Failed to assign to group")
                }
                channel.basicAck(delivery.envelope.deliveryTag, false)
                LOGGER.info("Successfully assigned group: $retryAssignGroupEvent")
            } catch (e: Exception) {
                LOGGER.error("Failed to assign group $retryAssignGroupEvent. Sending to DX Exchange", e)
                // Requeuing is set to false so that message is sent to dx exchange.
                channel.basicNack(delivery.envelope.deliveryTag, false, false)
            }
        }
        channel.basicConsume(QUEUE_RETRY_ASSIGN_GROUP, false, deliverCallback, CancelCallback { })
    }

    private fun declareExchangesAndQueues() {
        channel.exchangeDeclare(
                EXCHANGE_RETRY_ASSIGN_GROUP,
                BuiltinExchangeType.DIRECT,
                Durable.TRUE,
                AutoDelete.FALSE,
                emptyMap()
        )

        channel.exchangeDeclare(
                DX_EXCHANGE_RETRY_ASSIGN_GROUP,
                BuiltinExchangeType.DIRECT,
                Durable.TRUE,
                AutoDelete.FALSE,
                emptyMap()
        )

        channel.queueDeclare(
                QUEUE_RETRY_ASSIGN_GROUP,
                Durable.TRUE,
                Exclusive.FALSE,
                AutoDelete.FALSE,
                emptyMap()
        )

        channel.queueDeclare(
                DX_QUEUE_RETRY_ASSIGN_GROUP,
                Durable.TRUE,
                Exclusive.FALSE,
                AutoDelete.FALSE,
                emptyMap()
        )

        channel.queueBind(
                QUEUE_RETRY_ASSIGN_GROUP,
                EXCHANGE_RETRY_ASSIGN_GROUP,
                ROUTING_KEY_RETRY_ASSIGN_GROUP,
                mapOf("x-dead-letter-exchange" to DX_EXCHANGE_RETRY_ASSIGN_GROUP)

        )

        channel.queueBind(
                DX_QUEUE_RETRY_ASSIGN_GROUP,
                DX_EXCHANGE_RETRY_ASSIGN_GROUP,
                ROUTING_KEY_RETRY_ASSIGN_GROUP,
                mapOf(
                        "x-dead-letter-exchange" to EXCHANGE_RETRY_ASSIGN_GROUP,
                        "x-message-ttl" to Duration.of(config.retryAssignGroupIntervalMinutes, ChronoUnit.MINUTES).toMillis()
                )
        )
    }
}