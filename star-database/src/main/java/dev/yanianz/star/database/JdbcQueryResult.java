package dev.yanianz.star.database;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class JdbcQueryResult implements QueryResult {
    private final ResultSet rs;

    public JdbcQueryResult(@Nonnull ResultSet rs) {
        this.rs = rs;
    }

    @Override
    public boolean next() throws SQLException {
        return rs.next();
    }

    @Override
    @Nullable
    public String getString(@Nonnull String col) throws SQLException {
        return rs.getString(col);
    }

    @Override
    @Nullable
    public Integer getInt(@Nonnull String col) throws SQLException {
        int v = rs.getInt(col);
        return rs.wasNull() ? null : v;
    }

    @Override
    @Nullable
    public Double getDouble(@Nonnull String col) throws SQLException {
        double v = rs.getDouble(col);
        return rs.wasNull() ? null : v;
    }

    @Override
    @Nullable
    public Long getLong(@Nonnull String col) throws SQLException {
        long v = rs.getLong(col);
        return rs.wasNull() ? null : v;
    }

    @Override
    @Nullable
    public Boolean getBoolean(@Nonnull String col) throws SQLException {
        boolean v = rs.getBoolean(col);
        return rs.wasNull() ? null : v;
    }

    @Override
    public int getColumnCount() throws SQLException {
        return rs.getMetaData().getColumnCount();
    }

    @Override
    public void close() throws SQLException {
        rs.close();
    }
}
