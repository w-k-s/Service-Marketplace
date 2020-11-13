package com.wks.servicemarketplace.customerservice.adapters.db.converters;

import org.jooq.Converter;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class OffsetDateTimeConverter implements Converter<Timestamp, OffsetDateTime> {

    @Override
    public OffsetDateTime from(Timestamp t) {
        return t == null ? null : OffsetDateTime.ofInstant(t.toInstant(), ZoneId.of("UTC"));
    }

    @Override
    public Timestamp to(OffsetDateTime u) {
        return u == null ? null : Timestamp.from(u.toInstant());
    }

    @Override
    public Class<Timestamp> fromType() {
        return Timestamp.class;
    }

    @Override
    public Class<OffsetDateTime> toType() {
        return OffsetDateTime.class;
    }
}
