package dev.yanianz.star.database;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.SQLException;

/** Interface for database providers. */
public interface DatabaseProvider {
    void connect() throws SQLException;

    void disconnect();

    boolean isConnected();

    @Nonnull
    QueryResult query(@Nonnull String sql, Object... params) throws SQLException;

    int execute(@Nonnull String sql, Object... params) throws SQLException;

    @Nonnull
    Connection getConnection() throws SQLException;
}
