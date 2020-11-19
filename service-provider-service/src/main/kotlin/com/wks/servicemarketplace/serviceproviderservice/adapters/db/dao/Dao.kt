package com.wks.servicemarketplace.serviceproviderservice.adapters.db.dao

import com.wks.servicemarketplace.serviceproviderservice.core.Dao
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import java.sql.Connection
import javax.inject.Inject

open class BaseDao @Inject constructor(private val dataSource: DataSource) : Dao {
    final override fun connection(): Connection = dataSource.connection()
    protected fun create(connection: Connection) = DSL.using(connection, SQLDialect.POSTGRES)
}