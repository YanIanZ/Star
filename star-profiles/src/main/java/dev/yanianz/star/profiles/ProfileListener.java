package dev.yanianz.star.profiles;

import dev.yanianz.star.common.StarLogger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.logging.Level;

public final class ProfileListener implements Listener {
    private final ProfileManager manager;
    private final StarLogger logger;

    public ProfileListener(@Nonnull ProfileManager manager, @Nonnull StarLogger logger) {
        this.manager = manager;
        this.logger = logger;
    }

    @EventHandler
    public void onJoin(@Nonnull PlayerJoinEvent event) {
        manager.loadAll(event.getPlayer().getUniqueId()).thenAccept(profiles -> {
            if (profiles.isEmpty()) {
                Profile defaultProfile = Profile.builder("default")
                    .serverId("")
                    .createdAt(System.currentTimeMillis())
                    .build();
                manager.save(event.getPlayer(), defaultProfile);
                logger.log(Level.FINE, "Created default profile for " + event.getPlayer().getName());
            }
            logger.log(Level.FINE, "Loaded " + profiles.size() + " profiles for " + event.getPlayer().getName());
        });
    }

    @EventHandler
    public void onQuit(@Nonnull PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        manager.saveAll(uuid).thenRun(() -> {
            manager.evict(uuid);
            logger.log(Level.FINE, "Saved and evicted profiles for " + event.getPlayer().getName());
        });
    }
}
