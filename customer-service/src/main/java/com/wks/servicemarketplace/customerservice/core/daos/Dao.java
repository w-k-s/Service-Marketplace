package com.wks.servicemarketplace.customerservice.core.daos;

import java.sql.Connection;
import java.sql.SQLException;

public interface Dao {
    Connection getConnection() throws SQLException;
}
