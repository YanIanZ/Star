package dev.yanianz.star.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/** Wraps a JDBC ResultSet with convenience getters. */
public final class QueryResult implements AutoCloseable {
    private final ResultSet rs;

    public QueryResult(@Nonnull ResultSet rs) {
        this.rs = rs;
    }

    public boolean next() throws SQLException {
        return rs.next();
    }

    @Nullable
    public String getString(@Nonnull String col) throws SQLException {
        return rs.getString(col);
    }

    @Nullable
    public Integer getInt(@Nonnull String col) throws SQLException {
        int v = rs.getInt(col);
        return rs.wasNull() ? null : v;
    }

    @Nullable
    public Double getDouble(@Nonnull String col) throws SQLException {
        double v = rs.getDouble(col);
        return rs.wasNull() ? null : v;
    }

    @Nullable
    public Long getLong(@Nonnull String col) throws SQLException {
        long v = rs.getLong(col);
        return rs.wasNull() ? null : v;
    }

    @Nullable
    public Boolean getBoolean(@Nonnull String col) throws SQLException {
        boolean v = rs.getBoolean(col);
        return rs.wasNull() ? null : v;
    }

    public int getColumnCount() throws SQLException {
        return rs.getMetaData().getColumnCount();
    }

    @Override
    public void close() throws SQLException {
        rs.close();
    }
}
