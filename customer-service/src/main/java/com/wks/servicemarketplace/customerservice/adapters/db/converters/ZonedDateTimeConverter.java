package com.wks.servicemarketplace.customerservice.adapters.db.converters;

import org.jooq.Converter;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ZonedDateTimeConverter implements Converter<Timestamp, ZonedDateTime> {

    @Override
    public ZonedDateTime from(Timestamp t) {
        return t == null ? null : ZonedDateTime.ofInstant(t.toInstant(), ZoneId.of("UTC"));
    }

    @Override
    public Timestamp to(ZonedDateTime u) {
        return u == null ? null : Timestamp.from(u.toInstant());
    }

    @Override
    public Class<Timestamp> fromType() {
        return Timestamp.class;
    }

    @Override
    public Class<ZonedDateTime> toType() {
        return ZonedDateTime.class;
    }
}
