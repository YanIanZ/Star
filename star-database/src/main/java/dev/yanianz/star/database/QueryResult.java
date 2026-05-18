package dev.yanianz.star.database;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.SQLException;

/** Common result interface for all database providers. */
public interface QueryResult extends AutoCloseable {
    boolean next() throws SQLException;

    @Nullable String getString(@Nonnull String col) throws SQLException;
    @Nullable Integer getInt(@Nonnull String col) throws SQLException;
    @Nullable Double getDouble(@Nonnull String col) throws SQLException;
    @Nullable Long getLong(@Nonnull String col) throws SQLException;
    @Nullable Boolean getBoolean(@Nonnull String col) throws SQLException;

    int getColumnCount() throws SQLException;

    @Override void close() throws SQLException;
}
