package dev.yanianz.star.commands;

import org.bukkit.entity.Player;
import javax.annotation.Nonnull;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks per-player command cooldowns.
 * Use {@link #setCooldown(String, Player, int)} to start a cooldown
 * and {@link #isOnCooldown(String, Player)} to check.
 */
public final class CooldownManager {
    private final Map<String, Map<UUID, Long>> cooldowns = new ConcurrentHashMap<>();

    public void setCooldown(@Nonnull String command, @Nonnull Player player, int seconds) {
        cooldowns.computeIfAbsent(command, k -> new ConcurrentHashMap<>())
            .put(player.getUniqueId(), System.currentTimeMillis() + seconds * 1000L);
    }

    /**
     * Returns the remaining cooldown time in seconds, or {@code 0} if not on cooldown.
     */
    public long getRemaining(@Nonnull String command, @Nonnull Player player) {
        Map<UUID, Long> map = cooldowns.get(command);
        if (map == null) return 0;
        Long expiry = map.get(player.getUniqueId());
        if (expiry == null) return 0;
        long remaining = (expiry - System.currentTimeMillis()) / 1000;
        return Math.max(0, remaining);
    }

    public boolean isOnCooldown(@Nonnull String command, @Nonnull Player player) {
        return getRemaining(command, player) > 0;
    }

    public void clear(@Nonnull String command) {
        cooldowns.remove(command);
    }

    public void clearAll() {
        cooldowns.clear();
    }
}
