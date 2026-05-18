package dev.yanianz.star.packet;

import org.bukkit.entity.Player;
import javax.annotation.Nonnull;

public final class ProxyDetector {

    private ProxyDetector() {}

    public static boolean isBehind(@Nonnull Player player) {
        try {
            String forwarded = player.getAddress().getAddress().getHostAddress();
            return forwarded != null && (forwarded.startsWith("127.") || forwarded.startsWith("10.") || forwarded.startsWith("172.") || forwarded.startsWith("192.168."));
        } catch (Exception e) {
            return false;
        }
    }

    @Nonnull
    public static ProxyType detect(@Nonnull Player player) {
        // Simple detection via handshake data or player metadata
        // In practice, this checks channels registered by Bungee/Velocity
        try {
            if (player.getClass().getMethod("getSpigot").invoke(player) != null) return ProxyType.BUNGEE;
        } catch (Exception ignored) {}
        return ProxyType.NONE;
    }

    public enum ProxyType {
        NONE,
        BUNGEE,
        VELOCITY,
        WATERFALL
    }
}
