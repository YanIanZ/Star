package dev.yanianz.star.cache;

import javax.annotation.Nonnull;
import java.io.File;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public final class CacheBuilder<K, V> {
    private int maxSize = 1000;
    private long ttlMillis;
    private EvictionPolicy policy = EvictionPolicy.LRU;
    private CacheLoader<K, V> loader;
    private CachePersistence<K, V> persistence;
    private Logger logger;

    @Nonnull
    public static <K, V> CacheBuilder<K, V> create() {
        return new CacheBuilder<>();
    }

    @Nonnull
    public CacheBuilder<K, V> maxSize(int s) {
        this.maxSize = s;
        return this;
    }

    @Nonnull
    public CacheBuilder<K, V> expireAfterWrite(long duration, @Nonnull TimeUnit unit) {
        this.ttlMillis = unit.toMillis(duration);
        return this;
    }

    @Nonnull
    public CacheBuilder<K, V> expireAfterAccess(long duration, @Nonnull TimeUnit unit) {
        this.ttlMillis = unit.toMillis(duration);
        return this;
    }

    @Nonnull
    public CacheBuilder<K, V> evictionPolicy(@Nonnull EvictionPolicy p) {
        this.policy = p;
        return this;
    }

    @Nonnull
    public CacheBuilder<K, V> loader(@Nonnull CacheLoader<K, V> l) {
        this.loader = l;
        return this;
    }

    @Nonnull
    public CacheBuilder<K, V> persistence(@Nonnull CachePersistence<K, V> p) {
        this.persistence = p;
        return this;
    }

    @Nonnull
    public CacheBuilder<K, V> logger(@Nonnull Logger l) {
        this.logger = l;
        return this;
    }

    @Nonnull
    public CacheBuilder<K, V> jsonPersistence(@Nonnull File file, @Nonnull Type type) {
        this.persistence = new JsonCachePersistence<>(file, type);
        return this;
    }

    @Nonnull
    public Cache<K, V> build() {
        return new Cache<>(maxSize, ttlMillis, policy, loader, persistence, logger);
    }
}
