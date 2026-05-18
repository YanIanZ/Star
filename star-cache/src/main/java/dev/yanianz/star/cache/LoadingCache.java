package dev.yanianz.star.cache;

import javax.annotation.Nonnull;

public final class LoadingCache<K, V> {
    private final Cache<K, V> cache;

    public LoadingCache(@Nonnull Cache<K, V> cache) {
        this.cache = cache;
    }

    public V get(@Nonnull K key) {
        return cache.get(key);
    }

    public void put(@Nonnull K key, @Nonnull V value) {
        cache.put(key, value);
    }

    public void invalidate(@Nonnull K key) {
        cache.invalidate(key);
    }

    public int size() {
        return cache.size();
    }

    @Nonnull
    public CacheStats stats() {
        return cache.stats();
    }

    @Nonnull
    public Cache<K, V> getCache() {
        return cache;
    }
}
