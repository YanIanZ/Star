package dev.yanianz.star.packet;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.BiConsumer;

public final class PacketListener {

    private final Plugin plugin;
    private final Map<PacketType, List<BiConsumer<Player, Object>>> receiveHandlers = new HashMap<>();
    private final Map<PacketType, List<BiConsumer<Player, Object>>> sendHandlers = new HashMap<>();

    public PacketListener(@Nonnull Plugin plugin) {
        this.plugin = plugin;
    }

    public void onReceive(@Nonnull PacketType type, @Nonnull BiConsumer<Player, Object> handler) {
        receiveHandlers.computeIfAbsent(type, k -> new ArrayList<>()).add(handler);
    }

    public void onSend(@Nonnull PacketType type, @Nonnull BiConsumer<Player, Object> handler) {
        sendHandlers.computeIfAbsent(type, k -> new ArrayList<>()).add(handler);
    }

    @Nonnull
    public Map<PacketType, List<BiConsumer<Player, Object>>> getReceiveHandlers() {
        return receiveHandlers;
    }

    @Nonnull
    public Map<PacketType, List<BiConsumer<Player, Object>>> getSendHandlers() {
        return sendHandlers;
    }
}
