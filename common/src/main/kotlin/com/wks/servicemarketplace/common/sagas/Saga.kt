package com.wks.servicemarketplace.common.sagas

import com.wks.servicemarketplace.common.events.EventId
import java.time.Duration
import java.time.OffsetDateTime
import java.util.*

data class TransactionId private constructor(val value: UUID) {
    companion object {
        @JvmStatic
        fun of(uuid: UUID) = TransactionId(uuid)

        @JvmStatic
        fun fromString(uuidString: String) = TransactionId(UUID.fromString(uuidString))

        @JvmStatic
        fun random() = TransactionId(UUID.randomUUID())
    }

    override fun toString() = value.toString()
}

data class Saga(
        /**
         * Uniquely identifies the instance of the operation being performed e.g. movie-ticket-booking-123
         */
        val transactionId: TransactionId,
        /**
         * The name of the operation being performed e.g. PurchaseMovieTickets
         */
        val name: String,
        /**
         * Uniquely identifies the aggregate root involved in the operation e.g. bookingId: 123
         */
        val aggregateId: String,
        /**
         * The name of the aggregate root involved in the operation e.g. Booking
         */
        val aggregateType: String,
        /**
         * the current state of the transaction e.g. RESERVING_SEATS
         */
        val state: Saga.State,
        /**
         * the maximum time that this transaction can remain in this state (optional) e.g. RESERVING_SEATS (max: 1 min)
         */
        val deadline: Deadline?,
        /**
         * When deadline has passed, this event will be sent in the body of the deadline event.
         * e.g. If seats not reserved in 1 minute, cancel booking.
         */
        val eventId: EventId
) {
    interface State {
        fun stateName(): String
    }
}

sealed class Deadline
class DeadlineTime(val time: OffsetDateTime) : Deadline()
class DeadlineAfter(val duration: Duration) : Deadline()

