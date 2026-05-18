package dev.yanianz.star.npc;

import dev.yanianz.star.common.StarLogger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.logging.Level;

/** Manages NPC lifecycle. */
public final class NPCManager implements Listener {
    private final Plugin plugin;
    private final StarLogger logger;
    private final Map<String, NPC> npcs = new ConcurrentHashMap<>();
    private BukkitTask tickTask;

    public NPCManager(@Nonnull Plugin plugin) {
        this.plugin = plugin;
        this.logger = new StarLogger(plugin.getServer(), "npc");
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Nonnull
    public NPC create(@Nonnull Location location, @Nonnull NPCProfile profile) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        NPC npc = new NPC(id, profile, location);
        npc.spawn();
        npcs.put(id, npc);
        logger.log(Level.FINE, "Created NPC " + id + " (" + profile.name() + ")");
        return npc;
    }

    public void startTicking(int interval) {
        if (tickTask != null) tickTask.cancel();
        tickTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (NPC npc : npcs.values()) npc.tick();
        }, 0, interval);
    }

    public void stopTicking() {
        if (tickTask != null) { tickTask.cancel(); tickTask = null; }
    }

    @Nonnull
    public Optional<NPC> get(@Nonnull String id) { return Optional.ofNullable(npcs.get(id)); }

    @Nonnull
    public Collection<NPC> getAll() { return List.copyOf(npcs.values()); }

    public void delete(@Nonnull String id) {
        NPC npc = npcs.remove(id);
        if (npc != null) npc.remove();
    }

    public void delete(@Nonnull NPC npc) {
        npcs.remove(npc.getId());
        npc.remove();
    }

    public void deleteAll() {
        for (NPC npc : npcs.values()) npc.remove();
        npcs.clear();
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        npcs.values().stream()
            .filter(npc -> npc.getEntity() != null && npc.getEntity().getEntityId() == event.getRightClicked().getEntityId())
            .findFirst()
            .ifPresent(npc -> npc.handleInteract(event.getPlayer()));
    }
}
