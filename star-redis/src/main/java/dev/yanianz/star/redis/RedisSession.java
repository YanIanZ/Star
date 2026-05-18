package dev.yanianz.star.redis;

import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class RedisSession {
    private final RedisManager redis;
    private final String prefix;

    public RedisSession(@Nonnull RedisManager redis, @Nonnull String prefix) {
        this.redis = redis;
        this.prefix = prefix;
    }

    public void set(@Nonnull Player player, @Nonnull String key, @Nonnull String value) { redis.set(key(player, key), value); }
    @Nullable public String get(@Nonnull Player player, @Nonnull String key) { return redis.get(key(player, key)); }
    public void del(@Nonnull Player player, @Nonnull String key) { redis.del(key(player, key)); }

    private String key(Player player, String key) { return prefix + ":session:" + player.getUniqueId() + ":" + key; }
}
