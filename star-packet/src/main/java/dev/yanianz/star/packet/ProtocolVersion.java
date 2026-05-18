package dev.yanianz.star.packet;

import org.bukkit.entity.Player;
import javax.annotation.Nonnull;

public final class ProtocolVersion {

    private ProtocolVersion() {}

    public static int of(@Nonnull Player player) {
        try {
            return player.getProtocolVersion();
        } catch (Exception e) {
            return -1;
        }
    }

    public static boolean isAtLeast(@Nonnull Player player, int version) {
        return of(player) >= version;
    }
}
