package dev.yanianz.star.database.providers;

import dev.yanianz.star.database.*;
import javax.annotation.Nonnull;
import java.sql.*;

/** MySQL/MariaDB provider. */
public final class MySQLProvider implements DatabaseProvider {
    private final ConnectionPool pool;
    private boolean connected;

    public MySQLProvider(@Nonnull String host, int port, @Nonnull String database, @Nonnull String user, @Nonnull String password) {
        this.pool = new ConnectionPool("jdbc:mysql://" + host + ":" + port + "/" + database, user, password);
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
        prepare(ps, params);
        return new QueryResult(ps.executeQuery());
    }

    @Override
    public int execute(@Nonnull String sql, Object... params) throws SQLException {
        PreparedStatement ps = pool.getConnection().prepareStatement(sql);
        prepare(ps, params);
        return ps.executeUpdate();
    }

    private void prepare(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }

    @Override
    @Nonnull
    public Connection getConnection() throws SQLException {
        return pool.getConnection();
    }
}
