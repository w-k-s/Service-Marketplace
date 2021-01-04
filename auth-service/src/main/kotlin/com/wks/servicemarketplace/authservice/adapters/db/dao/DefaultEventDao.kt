package com.wks.servicemarketplace.authservice.adapters.db.dao

import com.wks.servicemarketplace.authservice.core.EventDao
import com.wks.servicemarketplace.common.events.EventEnvelope
import org.jooq.JSON
import org.jooq.impl.DSL.*
import java.sql.Connection
import javax.inject.Inject

class DefaultEventDao @Inject constructor(dataSource: DataSource) : BaseDao(dataSource), EventDao {
    override fun saveEvent(connection: Connection, event: EventEnvelope): Boolean {
        return create(connection).insertInto(table("events"),
                field("event_uuid"),
                field("event_type"),
                field("event_body"),
                field("entity_id"),
                field("entity_type")
        ).values(
                event.eventId.toString(),
                event.eventType.name,
                JSON.valueOf(event.eventBody),
                event.entityId,
                event.entityType
        ).execute() == 1
    }
}