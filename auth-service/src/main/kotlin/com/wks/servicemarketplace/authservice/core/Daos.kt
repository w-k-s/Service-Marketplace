package com.wks.servicemarketplace.authservice.core

import com.wks.servicemarketplace.authservice.core.events.EventEnvelope
import com.wks.servicemarketplace.authservice.core.utils.ModelValidator
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
    fun insertEvent(connection: Connection, event: EventEnvelope, idempotencyUUOD: IdempotencyUUID, publishAfter: Duration = Duration.ZERO): Boolean
    fun fetchUnpublishedEvents(connection: Connection): List<EventEnvelope>
}

data class IdempotencyUUID private constructor(@NotNull private val value: UUID) {
    companion object {
        @JvmStatic
        fun of(uuid: UUID) = ModelValidator.validate(IdempotencyUUID(uuid))

        @JvmStatic
        fun of(uuidString: String) = IdempotencyUUID(UUID.fromString(uuidString))
    }

    override fun toString() = value.toString()
}