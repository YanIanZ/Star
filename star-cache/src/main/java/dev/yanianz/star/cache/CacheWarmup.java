package dev.yanianz.star.cache;

import javax.annotation.Nonnull;
import java.util.Collection;

public final class CacheWarmup {
    private CacheWarmup() {}

    public static <K, V> void warmup(@Nonnull Cache<K, V> cache, @Nonnull Collection<K> keys) {
        for (K key : keys) cache.get(key);
    }

    @SafeVarargs
    public static <K, V> void warmup(@Nonnull Cache<K, V> cache, @Nonnull K... keys) {
        for (K key : keys) cache.get(key);
    }
}
