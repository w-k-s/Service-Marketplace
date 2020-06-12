package com.wks.servicemarketplace.accountservice.adapters.db.converters;

import org.jooq.DataType;
import org.jooq.impl.SQLDataType;

import java.time.ZonedDateTime;

public class JOOQConverters {
    public static DataType<ZonedDateTime> zonedDateTime() {
        return SQLDataType.TIMESTAMP.asConvertedDataType(new ZonedDateTimeConverter());
    }
}
