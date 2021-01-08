package com.wks.servicemarketplace.customerservice.adapters.db.dao;

import com.wks.servicemarketplace.common.events.EventEnvelope;
import com.wks.servicemarketplace.customerservice.core.daos.EventDao;

import javax.inject.Inject;
import java.sql.Connection;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

public class DefaultEventDao extends BaseDAO implements EventDao {

    @Inject
    public DefaultEventDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean saveEvent(Connection connection, EventEnvelope event) {
        return create(connection).insertInto(
                table("events"),
                field("event_uuid"),
                field("event_type"),
                field("event_body"),
                field("entity_id"),
                field("entity_type")
        ).values(
                event.getEventId().toString(),
                event.getEventType().toString(),
                event.getEventBody(),
                event.getEntityId(),
                event.getEntityType()
        ).execute() == 1;
    }
}
