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

    @Nullable
    public String getRaw(@Nonnull String key) { return redis.get(key(key)); }

    public void setRaw(@Nonnull String key, @Nonnull String value) { redis.set(key(key), value); }

    public void setRaw(@Nonnull String key, @Nonnull String value, int ttlSeconds) {
        redis.set(key(key), value);
        redis.expire(key(key), ttlSeconds);
    }

    public void del(@Nonnull String key) { redis.del(key(key)); }

    public boolean exists(@Nonnull String key) { return redis.exists(key(key)); }

    @Nonnull
    public String getNamespace() { return namespace; }

    private String key(String k) { return namespace + ":" + k; }
}
