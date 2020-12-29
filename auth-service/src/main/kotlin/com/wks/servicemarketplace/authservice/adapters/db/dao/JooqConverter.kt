package com.wks.servicemarketplace.authservice.adapters.db.dao

import org.jooq.Converter
import java.sql.Timestamp
import java.time.OffsetDateTime
import java.time.ZoneOffset

internal class OffsetDateTimeConverter : Converter<Timestamp, OffsetDateTime> {
    override fun from(t: Timestamp?): OffsetDateTime? = t?.let { OffsetDateTime.ofInstant(t.toInstant(), ZoneOffset.UTC) }
    override fun to(u: OffsetDateTime?) = u?.let { Timestamp.from(it.toInstant()) }
    override fun fromType() = Timestamp::class.java
    override fun toType() = OffsetDateTime::class.java
}