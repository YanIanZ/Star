package dev.yanianz.star.profiles;

import dev.yanianz.star.common.StarLogger;
import dev.yanianz.star.profiles.storage.ProfileRepository;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public final class ProfileManager {
    private final Plugin plugin;
    private final StarLogger logger;
    private final ProfileRepository repository;
    private final Map<UUID, Map<String, Profile>> playerProfiles = new ConcurrentHashMap<>();
    private final Map<UUID, String> activeProfiles = new ConcurrentHashMap<>();
    private int autoSaveTaskId = -1;

    public ProfileManager(@Nonnull Plugin plugin, @Nonnull ProfileRepository repository) {
        this.plugin = plugin;
        this.logger = new StarLogger(plugin.getServer(), "profiles");
        this.repository = repository;
    }

    public void save(@Nonnull Player player, @Nonnull Profile profile) {
        playerProfiles.computeIfAbsent(player.getUniqueId(), k -> new ConcurrentHashMap<>()).put(profile.getName(), profile);
        repository.save(player.getUniqueId(), profile);
        logger.log(Level.FINE, "Saved profile " + profile.getName() + " for " + player.getName());
    }

    @Nonnull public Optional<Profile> get(@Nonnull UUID playerUuid, @Nonnull String name) {
        return Optional.ofNullable(playerProfiles.getOrDefault(playerUuid, Map.of()).get(name));
    }

    @Nonnull public Optional<Profile> get(@Nonnull Player player, @Nonnull String name) {
        return get(player.getUniqueId(), name);
    }

    @Nonnull public CompletableFuture<List<Profile>> loadAll(@Nonnull UUID playerUuid) {
        return repository.loadAll(playerUuid).thenApply(profiles -> {
            Map<String, Profile> map = playerProfiles.computeIfAbsent(playerUuid, k -> new ConcurrentHashMap<>());
            for (Profile profile : profiles) {
                map.put(profile.getName(), profile);
            }
            if (!profiles.isEmpty() && !activeProfiles.containsKey(playerUuid)) {
                activeProfiles.put(playerUuid, profiles.get(0).getName());
            }
            logger.log(Level.FINE, "Loaded " + profiles.size() + " profiles for " + playerUuid);
            return profiles;
        });
    }

    @Nonnull public Collection<String> getProfileNames(@Nonnull Player player) {
        return playerProfiles.getOrDefault(player.getUniqueId(), Map.of()).keySet();
    }

    public void switchTo(@Nonnull Player player, @Nonnull String profileName) {
        UUID uuid = player.getUniqueId();
        Optional<Profile> current = getActiveProfile(player)
            .flatMap(name -> get(uuid, name));
        current.ifPresent(prev -> {
            prev.setLocation(player.getLocation());
            repository.save(uuid, prev);
        });

        get(uuid, profileName).ifPresent(profile -> {
            activeProfiles.put(uuid, profileName);
            profile.getInventory().forEach(item -> { if (item != null) player.getInventory().addItem(item); });
            profile.getLocation().ifPresent(player::teleport);
            logger.log(Level.INFO, player.getName() + " switched to profile " + profileName);
        });
    }

    @Nonnull public Optional<String> getActiveProfile(@Nonnull Player player) {
        return Optional.ofNullable(activeProfiles.get(player.getUniqueId()));
    }

    public void delete(@Nonnull Player player, @Nonnull String name) {
        UUID uuid = player.getUniqueId();
        playerProfiles.getOrDefault(uuid, Map.of()).remove(name);
        if (name.equals(activeProfiles.get(uuid))) activeProfiles.remove(uuid);
        repository.delete(uuid, name);
    }

    public int getProfileCount(@Nonnull Player player) {
        return playerProfiles.getOrDefault(player.getUniqueId(), Map.of()).size();
    }

    @Nonnull public CompletableFuture<Void> saveAll(@Nonnull UUID playerUuid) {
        Map<String, Profile> profiles = playerProfiles.get(playerUuid);
        if (profiles == null || profiles.isEmpty()) return CompletableFuture.completedFuture(null);
        return CompletableFuture.allOf(
            profiles.values().stream()
                .map(p -> repository.save(playerUuid, p))
                .toArray(CompletableFuture[]::new)
        );
    }

    public void evict(@Nonnull UUID playerUuid) {
        playerProfiles.remove(playerUuid);
        activeProfiles.remove(playerUuid);
    }

    public void startAutoSave(int intervalSeconds) {
        if (autoSaveTaskId != -1) return;
        autoSaveTaskId = plugin.getServer().getScheduler().runTaskTimerAsynchronously(
            plugin,
            () -> {
                for (UUID uuid : playerProfiles.keySet()) {
                    saveAll(uuid);
                }
            },
            20L * intervalSeconds,
            20L * intervalSeconds
        ).getTaskId();
        logger.log(Level.INFO, "Auto-save started with interval " + intervalSeconds + "s");
    }

    public void stopAutoSave() {
        if (autoSaveTaskId != -1) {
            plugin.getServer().getScheduler().cancelTask(autoSaveTaskId);
            autoSaveTaskId = -1;
            logger.log(Level.INFO, "Auto-save stopped");
        }
    }
}
