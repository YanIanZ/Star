package dev.yanianz.star.cache;

import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public final class PlayerCache<V> {
    private final Cache<UUID, V> cache;

    public PlayerCache(@Nonnull Cache<UUID, V> cache) {
        this.cache = cache;
    }

    @Nullable
    public V get(@Nonnull Player player) {
        return cache.get(player.getUniqueId());
    }

    public void put(@Nonnull Player player, @Nonnull V value) {
        cache.put(player.getUniqueId(), value);
    }

    public void invalidate(@Nonnull Player player) {
        cache.invalidate(player.getUniqueId());
    }

    @Nonnull
    public Cache<UUID, V> getCache() {
        return cache;
    }
}
