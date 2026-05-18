package dev.yanianz.star.redis;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class RedisCache<V> {
    private final RedisManager redis;
    private final String namespace;

    public RedisCache(@Nonnull RedisManager redis, @Nonnull String namespace) {
        this.redis = redis;
        this.namespace = namespace;
    }

    @Nullable public V get(@Nonnull String key) { return null; }
    public void set(@Nonnull String key, @Nonnull String value) { redis.set(namespace + ":" + key, value); }
    public void del(@Nonnull String key) { redis.del(namespace + ":" + key); }
    public boolean exists(@Nonnull String key) { return redis.exists(namespace + ":" + key); }
    @Nonnull public String getNamespace() { return namespace; }
}
