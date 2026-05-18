package dev.yanianz.star.database;

import javax.annotation.Nonnull;
import java.util.*;

/** Fluent SQL query builder. */
public final class QueryBuilder {
    private String table;
    private final List<String> columns = new ArrayList<>();
    private final List<String> wheres = new ArrayList<>();
    private final List<Object> params = new ArrayList<>();
    private String orderBy;
    private Integer limit, offset;

    private QueryBuilder() {}

    @Nonnull
    public static QueryBuilder select(@Nonnull String... columns) {
        QueryBuilder qb = new QueryBuilder();
        Collections.addAll(qb.columns, columns);
        return qb;
    }

    @Nonnull
    public QueryBuilder from(@Nonnull String table) {
        this.table = table;
        return this;
    }

    @Nonnull
    public QueryBuilder where(@Nonnull String condition, Object... params) {
        this.wheres.add(condition);
        Collections.addAll(this.params, params);
        return this;
    }

    @Nonnull
    public QueryBuilder orderBy(@Nonnull String order) {
        this.orderBy = order;
        return this;
    }

    @Nonnull
    public QueryBuilder limit(int limit) {
        this.limit = limit;
        return this;
    }

    @Nonnull
    public QueryBuilder offset(int offset) {
        this.offset = offset;
        return this;
    }

    @Nonnull
    public String build() {
        StringBuilder sb = new StringBuilder("SELECT ")
                .append(String.join(", ", columns))
                .append(" FROM ")
                .append(table);
        if (!wheres.isEmpty()) {
            sb.append(" WHERE ").append(String.join(" AND ", wheres));
        }
        if (orderBy != null) {
            sb.append(" ORDER BY ").append(orderBy);
        }
        if (limit != null) {
            sb.append(" LIMIT ").append(limit);
        }
        if (offset != null) {
            sb.append(" OFFSET ").append(offset);
        }
        return sb.toString();
    }

    @Nonnull
    public Object[] getParams() {
        return params.toArray();
    }
}
