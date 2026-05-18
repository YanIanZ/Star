package dev.yanianz.star.cache;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public final class AsyncCacheOps {
    private AsyncCacheOps() {}

    @Nonnull
    public static <K, V> CompletableFuture<V> getAsync(@Nonnull Cache<K, V> cache, @Nonnull K key) {
        return CompletableFuture.supplyAsync(() -> cache.get(key));
    }

    @Nonnull
    public static <K, V> CompletableFuture<Void> putAsync(@Nonnull Cache<K, V> cache, @Nonnull K key, @Nonnull V value) {
        return CompletableFuture.runAsync(() -> cache.put(key, value));
    }
}
