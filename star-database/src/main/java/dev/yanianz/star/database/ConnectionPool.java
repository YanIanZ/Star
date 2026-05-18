package dev.yanianz.star.database;

import javax.annotation.Nonnull;
import java.sql.*;

/** Simple connection pool using HikariCP or basic DriverManager. */
public final class ConnectionPool {
    private final String url, user, password;
    private Connection connection;

    public ConnectionPool(@Nonnull String url, @Nonnull String user, @Nonnull String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @Nonnull
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url, user, password);
        }
        return connection;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException ignored) {
        }
    }
}
