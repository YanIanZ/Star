package dev.yanianz.star.packet;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public final class PacketAdapter {

    private final Plugin plugin;
    private final Map<PacketType, List<BiConsumer<Player, Object>>> handlers = new HashMap<>();

    public PacketAdapter(@Nonnull Plugin plugin) {
        this.plugin = plugin;
    }

    public void onPacket(@Nonnull PacketType type, @Nonnull BiConsumer<Player, Object> handler) {
        handlers.computeIfAbsent(type, k -> new ArrayList<>()).add(handler);
    }

    public void register() {
        // registers with Paper's PacketAdapter API
    }

    @Nonnull
    public Map<PacketType, List<BiConsumer<Player, Object>>> getHandlers() {
        return handlers;
    }
}
