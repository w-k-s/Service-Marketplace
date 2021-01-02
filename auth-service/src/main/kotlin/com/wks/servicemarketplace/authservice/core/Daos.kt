package com.wks.servicemarketplace.authservice.core

import com.wks.servicemarketplace.authservice.core.events.EventEnvelope
import com.wks.servicemarketplace.authservice.core.events.EventId
import java.sql.Connection
import java.sql.SQLException
import java.time.Duration
import java.util.*
import javax.validation.constraints.NotNull

interface Dao {
    @Throws(SQLException::class)
    fun connection(): Connection
}

interface EventDao : Dao {
    fun saveEventForPublishing(connection: Connection, event: EventEnvelope, idempotencyUUID: IdempotencyUUID? = null, publishAfter: Duration = Duration.ZERO): Boolean
    fun fetchUnpublishedEvents(connection: Connection): List<EventEnvelope>
    fun setPublished(connection: Connection, eventId: EventId): Boolean
}

data class IdempotencyUUID private constructor(private val value: UUID) {
    companion object {
        @JvmStatic
        fun of(uuid: UUID) = IdempotencyUUID(uuid)

        @JvmStatic
        fun of(uuidString: String) = IdempotencyUUID(UUID.fromString(uuidString))
    }

    override fun toString() = value.toString()
}