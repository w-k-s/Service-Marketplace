package com.wks.servicemarketplace.accountservice.adapters.db.dao;

import com.wks.servicemarketplace.accountservice.core.daos.Dao;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.SQLException;

public class BaseDAO implements Dao {

    private final DataSource dataSource;

    public BaseDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected final DSLContext create(Connection connection) {
        return DSL.using(connection, SQLDialect.POSTGRES);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
