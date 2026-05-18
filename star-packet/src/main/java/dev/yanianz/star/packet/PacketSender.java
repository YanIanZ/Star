package dev.yanianz.star.packet;

import org.bukkit.entity.Player;
import javax.annotation.Nonnull;

public final class PacketSender {

    private PacketSender() {}

    public static void sendPacket(@Nonnull Player player, @Nonnull Object packet) {
        // Packet sending requires NMS — uses Paper's native packet API or Reflection
        // This is a placeholder; actual sending depends on the adapter in use
    }
}
