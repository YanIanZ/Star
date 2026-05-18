package dev.yanianz.star.holograms;

import dev.yanianz.star.common.StarLogger;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public final class HologramManager {
    private final Plugin plugin;
    private final StarLogger logger;
    private final Map<String, Hologram> holograms = new ConcurrentHashMap<>();

    public HologramManager(@Nonnull Plugin plugin) {
        this.plugin = plugin;
        this.logger = new StarLogger(plugin.getServer(), "holograms");
    }

    @Nonnull
    public Hologram create(@Nonnull Location location, @Nonnull String... lines) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        Hologram holo = new Hologram(id, location);
        for (String line : lines) holo.addLine(new HologramLine(Component.text(line)));
        holograms.put(id, holo);
        logger.log(Level.FINE, "Created hologram " + id);
        return holo;
    }

    @Nonnull
    public Optional<Hologram> get(@Nonnull String id) { return Optional.ofNullable(holograms.get(id)); }

    @Nonnull
    public Collection<Hologram> getAll() { return List.copyOf(holograms.values()); }

    public void delete(@Nonnull String id) {
        Hologram holo = holograms.remove(id);
        if (holo != null) holo.remove();
    }

    public void delete(@Nonnull Hologram hologram) {
        holograms.remove(hologram.getId());
        hologram.remove();
    }

    public void deleteAll() {
        for (Hologram holo : holograms.values()) holo.remove();
        holograms.clear();
    }

    public int getCount() { return holograms.size(); }
}
