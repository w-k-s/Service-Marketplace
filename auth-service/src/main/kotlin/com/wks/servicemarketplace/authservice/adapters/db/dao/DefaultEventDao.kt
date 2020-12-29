package com.wks.servicemarketplace.authservice.adapters.db.dao

import com.wks.servicemarketplace.authservice.core.EventDao
import com.wks.servicemarketplace.authservice.core.IdempotencyUUID
import com.wks.servicemarketplace.authservice.core.events.EventEnvelope
import com.wks.servicemarketplace.authservice.core.events.EventId
import com.wks.servicemarketplace.authservice.core.events.EventType
import org.jooq.DatePart
import org.jooq.JSON
import org.jooq.impl.DSL
import org.jooq.impl.DSL.*
import java.sql.Connection
import java.sql.Timestamp
import java.time.Duration
import javax.inject.Inject

class DefaultEventDao @Inject constructor(dataSource: DataSource) : BaseDao(dataSource), EventDao {

    override fun saveEventForPublishing(connection: Connection, event: EventEnvelope, idempotencyUUID: IdempotencyUUID?, publishAfter: Duration): Boolean {
        return create(connection).insertInto(table("events"),
                field("event_uuid"),
                field("event_type"),
                field("event_body"),
                field("entity_id"),
                field("entity_type"),
                field("idempotency_id"),
                field("publish_after")
        ).values(
                event.eventId.toString(),
                event.eventType.name,
                JSON.valueOf(event.eventBody),
                event.entityId,
                event.entityType,
                idempotencyUUID?.toString() ?: `val`(null, String::class.java),
                create(connection).select(
                        timestampAdd(currentTimestamp(), publishAfter.toSeconds(), DatePart.SECOND)
                ).fetchOne().into(Timestamp::class.java)
        ).execute() == 1
    }

    override fun fetchUnpublishedEvents(connection: Connection): List<EventEnvelope> {
        return create(connection).select(
                field("event_uuid"),
                field("event_type"),
                field("event_body"),
                field("entity_id"),
                field("entity_type")
        ).from(
                table("events")
        ).where(
                field("published").eq(false),
                field("publish_after").lt(currentTimestamp())
        ).fetch {
            EventEnvelope(
                    EventId.fromString(it.get("event_uuid", String::class.java)),
                    EventType.valueOf(it.get("event_type", String::class.java)),
                    it.get("event_body", String::class.java),
                    it.get("entity_id", String::class.java),
                    it.get("entity_type", String::class.java)
            )
        }
    }

    override fun setPublished(connection: Connection, eventId: EventId): Boolean {
        return create(connection).update(table("events"))
                .set(field("published"), true)
                .where(
                        field("published").eq(false),
                        field("event_uuid").eq(eventId.toString())
                ).execute() == 1
    }
}