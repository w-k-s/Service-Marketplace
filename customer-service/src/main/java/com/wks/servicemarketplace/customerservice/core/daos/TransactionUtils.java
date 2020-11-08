package com.wks.servicemarketplace.customerservice.core.daos;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;

public class TransactionUtils {

    public static void rollback(Connection connection, Consumer<SQLException> rollbackFailureHandler) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.rollback();
            }
        } catch (SQLException e) {
            if (rollbackFailureHandler != null) {
                rollbackFailureHandler.accept(e);
            } else {
                e.printStackTrace();
            }
        }
    }

    public static void rollback(Connection connection) {
        TransactionUtils.rollback(connection, null);
    }

    public static void enableAutocommit(Connection connection, Consumer<SQLException> enableAutocommitExceptionHandler) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            if (enableAutocommitExceptionHandler != null) {
                enableAutocommitExceptionHandler.accept(e);
            } else {
                e.printStackTrace();
            }
        }
    }

    public static void enableAutocommit(Connection connection) {
        TransactionUtils.enableAutocommit(connection, null);
    }

    public static Connection beginTransaction(Connection connection) throws SQLException {
        connection.setAutoCommit(false);
        return connection;
    }
}
