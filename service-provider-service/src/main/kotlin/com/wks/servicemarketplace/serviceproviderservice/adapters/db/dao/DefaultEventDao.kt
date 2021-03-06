package com.wks.servicemarketplace.serviceproviderservice.adapters.db.dao

import com.wks.servicemarketplace.common.events.EventEnvelope
import com.wks.servicemarketplace.serviceproviderservice.core.EventDao
import org.jooq.JSON
import org.jooq.impl.DSL.*
import java.sql.Connection

class DefaultEventDao constructor(dataSource: DataSource) : BaseDao(dataSource), EventDao {
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