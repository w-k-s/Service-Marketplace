package com.wks.servicemarketplace.authservice.adapters.db.dao

import com.wks.servicemarketplace.authservice.adapters.db.dao.DataSource
import com.wks.servicemarketplace.authservice.core.Dao
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import java.sql.Connection
import javax.inject.Inject

open class BaseDao @Inject constructor(private val dataSource: DataSource) : Dao {
    final override fun connection(): Connection = dataSource.connection()
    protected fun create(connection: Connection) = DSL.using(connection, SQLDialect.POSTGRES)
}