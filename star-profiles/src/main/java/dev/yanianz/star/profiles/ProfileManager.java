package dev.yanianz.star.profiles;

import dev.yanianz.star.common.StarLogger;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public final class ProfileManager {
    private final Plugin plugin;
    private final StarLogger logger;
    private final Map<UUID, Map<String, Profile>> playerProfiles = new ConcurrentHashMap<>();
    private final Map<UUID, String> activeProfiles = new ConcurrentHashMap<>();

    public ProfileManager(@Nonnull Plugin plugin) {
        this.plugin = plugin;
        this.logger = new StarLogger(plugin.getServer(), "profiles");
    }

    public void save(@Nonnull Player player, @Nonnull Profile profile) {
        playerProfiles.computeIfAbsent(player.getUniqueId(), k -> new ConcurrentHashMap<>()).put(profile.getName(), profile);
        logger.log(Level.FINE, "Saved profile " + profile.getName() + " for " + player.getName());
    }

    @Nonnull public Optional<Profile> get(@Nonnull Player player, @Nonnull String name) {
        return Optional.ofNullable(playerProfiles.getOrDefault(player.getUniqueId(), Map.of()).get(name));
    }

    @Nonnull public Collection<String> getProfileNames(@Nonnull Player player) {
        return playerProfiles.getOrDefault(player.getUniqueId(), Map.of()).keySet();
    }

    public void switchTo(@Nonnull Player player, @Nonnull String profileName) {
        get(player, profileName).ifPresent(profile -> {
            activeProfiles.put(player.getUniqueId(), profileName);
            profile.getInventory().forEach(item -> { if (item != null) player.getInventory().addItem(item); });
            profile.getLocation().ifPresent(player::teleport);
            logger.log(Level.INFO, player.getName() + " switched to profile " + profileName);
        });
    }

    @Nonnull public Optional<String> getActiveProfile(@Nonnull Player player) {
        return Optional.ofNullable(activeProfiles.get(player.getUniqueId()));
    }

    public void delete(@Nonnull Player player, @Nonnull String name) {
        playerProfiles.getOrDefault(player.getUniqueId(), Map.of()).remove(name);
        if (name.equals(activeProfiles.get(player.getUniqueId()))) activeProfiles.remove(player.getUniqueId());
    }

    public int getProfileCount(@Nonnull Player player) {
        return playerProfiles.getOrDefault(player.getUniqueId(), Map.of()).size();
    }
}
