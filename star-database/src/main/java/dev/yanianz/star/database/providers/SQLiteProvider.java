package dev.yanianz.star.database.providers;

import dev.yanianz.star.database.*;
import javax.annotation.Nonnull;
import java.io.File;
import java.sql.*;

/** SQLite file-based provider. */
public final class SQLiteProvider implements DatabaseProvider {
    private final ConnectionPool pool;
    private boolean connected;

    public SQLiteProvider(@Nonnull File file) {
        this.pool = new ConnectionPool("jdbc:sqlite:" + file.getAbsolutePath(), "", "");
    }

    @Override
    public void connect() throws SQLException {
        pool.getConnection();
        connected = true;
    }

    @Override
    public void disconnect() {
        pool.close();
        connected = false;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    @Nonnull
    public QueryResult query(@Nonnull String sql, Object... params) throws SQLException {
        PreparedStatement ps = pool.getConnection().prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
        return new QueryResult(ps.executeQuery());
    }

    @Override
    public int execute(@Nonnull String sql, Object... params) throws SQLException {
        PreparedStatement ps = pool.getConnection().prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
        return ps.executeUpdate();
    }

    @Override
    @Nonnull
    public Connection getConnection() throws SQLException {
        return pool.getConnection();
    }
}
