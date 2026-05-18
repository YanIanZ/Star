package dev.yanianz.star.packet;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class BrandMessenger implements PluginMessageListener {

    private final Plugin plugin;
    private final Map<UUID, String> playerBrands = new HashMap<>();

    public BrandMessenger(@Nonnull Plugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "minecraft:brand", this);
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "minecraft:brand");
    }

    @Nonnull
    public Optional<String> getBrand(@Nonnull Player player) {
        return Optional.ofNullable(playerBrands.get(player.getUniqueId()));
    }

    @Override
    public void onPluginMessageReceived(@Nonnull String channel, @Nonnull Player player, @Nonnull byte[] message) {
        playerBrands.put(player.getUniqueId(), new String(message));
    }
}
