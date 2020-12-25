package com.wks.servicemarketplace.authservice.adapters.db.dao

import org.jooq.Converter
import java.sql.Timestamp
import java.time.OffsetDateTime
import java.time.ZoneId

internal class OffsetDateTimeConverter : Converter<Timestamp, OffsetDateTime> {
    override fun from(t: Timestamp?): OffsetDateTime? {
        return if (t == null) null else OffsetDateTime.ofInstant(t.toInstant(), ZoneId.of("UTC"))
    }

    override fun to(u: OffsetDateTime?): Timestamp? {
        return if (u == null) null else Timestamp.from(u.toInstant())
    }

    override fun fromType(): Class<Timestamp> = Timestamp::class.java

    override fun toType(): Class<OffsetDateTime> = OffsetDateTime::class.java
}
