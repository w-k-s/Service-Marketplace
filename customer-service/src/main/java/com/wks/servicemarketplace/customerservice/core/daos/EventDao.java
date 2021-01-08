package com.wks.servicemarketplace.customerservice.core.daos;

import com.wks.servicemarketplace.common.events.EventEnvelope;

import java.sql.Connection;

public interface EventDao extends Dao {
    boolean saveEvent(Connection connection, EventEnvelope event);
}
