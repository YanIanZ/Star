package dev.yanianz.star.database.async;

import dev.yanianz.star.database.*;
import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.util.concurrent.*;

/** Wraps database operations in CompletableFuture for async execution. */
public final class AsyncDatabase {
    private final DatabaseProvider provider;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public AsyncDatabase(@Nonnull DatabaseProvider provider) {
        this.provider = provider;
    }

    @Nonnull
    public CompletableFuture<QueryResult> query(@Nonnull String sql, Object... params) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return provider.query(sql, params);
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        }, executor);
    }

    @Nonnull
    public CompletableFuture<Integer> execute(@Nonnull String sql, Object... params) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return provider.execute(sql, params);
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        }, executor);
    }

    public void shutdown() {
        executor.shutdown();
    }
}
