package dev.yanianz.star.holograms;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.entity.Display.Billboard;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public final class Hologram {
    private final String id;
    private final Location location;
    private final List<HologramLine> lines = new CopyOnWriteArrayList<>();
    private final List<TextDisplay> displays = new ArrayList<>();
    private final Set<UUID> viewers = new HashSet<>();
    private HologramAnimation animation;

    public Hologram(@Nonnull String id, @Nonnull Location location) {
        this.id = id; this.location = location.clone();
    }

    @Nonnull public String getId() { return id; }
    @Nonnull public Location getLocation() { return location; }
    public int getLineCount() { return lines.size(); }

    public void addLine(@Nonnull HologramLine line) { lines.add(line); refreshDisplays(); }
    public void setLine(int index, @Nonnull HologramLine line) { lines.set(index, line); refreshDisplays(); }
    public void insertLine(int index, @Nonnull HologramLine line) { lines.add(index, line); refreshDisplays(); }
    public void removeLine(int index) { lines.remove(index); refreshDisplays(); }
    public void clearLines() { lines.clear(); clearDisplays(); }
    @Nonnull public List<HologramLine> getLines() { return List.copyOf(lines); }

    public void setAnimation(@Nonnull HologramAnimation animation) { this.animation = animation; }
    @Nonnull public Optional<HologramAnimation> getAnimation() { return Optional.ofNullable(animation); }

    public void show(@Nonnull Player player) {
        if (viewers.add(player.getUniqueId())) spawnDisplays();
    }

    public void hide(@Nonnull Player player) {
        viewers.remove(player.getUniqueId());
        clearDisplays();
    }

    public boolean isVisibleTo(@Nonnull Player player) { return viewers.contains(player.getUniqueId()); }

    private void spawnDisplays() {
        clearDisplays();
        for (int i = 0; i < lines.size(); i++) {
            Location loc = location.clone().add(0, -i * 0.3, 0);
            TextDisplay td = loc.getWorld().spawn(loc, TextDisplay.class);
            td.setBillboard(Billboard.CENTER);
            td.setSeeThrough(false);
            Component text = animation != null ? animation.apply(lines.get(i), 0) : lines.get(i).getText();
            if (text != null) td.text(text);
            displays.add(td);
        }
    }

    private void refreshDisplays() {
        clearDisplays();
        if (!viewers.isEmpty()) spawnDisplays();
    }

    private void clearDisplays() {
        for (TextDisplay td : displays) td.remove();
        displays.clear();
    }

    public void remove() {
        clearDisplays();
        viewers.clear();
        lines.clear();
    }
}
