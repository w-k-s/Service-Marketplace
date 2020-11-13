package com.wks.servicemarketplace.customerservice.adapters.db.converters;

import org.jooq.DataType;
import org.jooq.impl.SQLDataType;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

public class JOOQConverters {
    public static DataType<OffsetDateTime> offsetDateTime() {
        return SQLDataType.TIMESTAMP.asConvertedDataType(new OffsetDateTimeConverter());
    }
}
