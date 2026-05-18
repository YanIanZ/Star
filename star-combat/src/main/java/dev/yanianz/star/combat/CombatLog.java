package dev.yanianz.star.combat;

import org.bukkit.entity.Player;
import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/** Tracks player combat state and taggers. */
public final class CombatLog {
    private final Map<UUID, Long> combatTimers = new ConcurrentHashMap<>();
    private final Map<UUID, UUID> lastDamager = new ConcurrentHashMap<>();
    private final int durationSeconds;

    public CombatLog(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public void enter(@Nonnull Player player) {
        combatTimers.put(player.getUniqueId(), System.currentTimeMillis() + durationSeconds * 1000L);
    }

    public void exit(@Nonnull Player player) {
        combatTimers.remove(player.getUniqueId());
    }

    public boolean isInCombat(@Nonnull Player player) {
        Long expiry = combatTimers.get(player.getUniqueId());
        return expiry != null && expiry > System.currentTimeMillis();
    }

    public int getRemaining(@Nonnull Player player) {
        Long expiry = combatTimers.get(player.getUniqueId());
        return expiry == null ? 0 : (int) Math.max(0, (expiry - System.currentTimeMillis()) / 1000);
    }

    public void tag(@Nonnull Player victim, @Nonnull Player attacker) {
        lastDamager.put(victim.getUniqueId(), attacker.getUniqueId());
    }

    @Nonnull
    public Optional<UUID> getTaggedBy(@Nonnull Player player) {
        return Optional.ofNullable(lastDamager.get(player.getUniqueId()));
    }

    public void clear() {
        combatTimers.clear();
        lastDamager.clear();
    }
}
