package com.wks.servicemarketplace.authservice.adapters.db.dao

import com.wks.servicemarketplace.authservice.core.EventDao
import com.wks.servicemarketplace.authservice.core.IdempotencyUUID
import com.wks.servicemarketplace.authservice.core.events.EventEnvelope
import org.jooq.DatePart
import org.jooq.impl.DSL.*
import java.sql.Connection
import java.time.Duration
import javax.inject.Inject

class DefaultEventDao @Inject constructor(dataSource: DataSource) : BaseDao(dataSource), EventDao {

    override fun insertEvent(connection: Connection, event: EventEnvelope, idempotencyUUID: IdempotencyUUID, publishAfter: Duration): Boolean {
        return create(connection).insertInto(table("events"),
                field("event_uuid"),
                field("event_type"),
                field("event_body"),
                field("entity_id"),
                field("entity_type"),
                field("idempotency_id"),
                field("publish_after")
        ).values(
                event.eventId,
                event.eventType,
                event.eventBody,
                event.entityId,
                event.entityType,
                idempotencyUUID.toString(),
                create(connection).select(timestampAdd(currentTimestamp(), publishAfter.toMillis(), DatePart.MILLISECOND))
        ).execute() == 1
    }

    override fun fetchUnpublishedEvents(connection: Connection): List<EventEnvelope> {
        return create(connection).select(
                field("event_uuid"),
                field("event_type"),
                field("event_body"),
                field("entity_id"),
                field("entity_type")
        ).where(
                field("published").eq(false),
                field("publish_after").lt(currentTimestamp())
        ).fetch {
            EventEnvelope(
                    it.get("event_uuid", String::class.java),
                    it.get("event_type", String::class.java),
                    it.get("event_body", String::class.java),
                    it.get("entity_id", String::class.java),
                    it.get("entity_type", String::class.java)
            )
        }
    }
}