package dev.yanianz.star.packet;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import javax.annotation.Nonnull;

public final class TransferHelper {

    private TransferHelper() {}

    public static void transfer(@Nonnull Player player, @Nonnull String host, int port) {
        try {
            player.getClass().getMethod("transfer", String.class, int.class).invoke(player, host, port);
        } catch (Exception e) {
            player.kick(Component.text("Transferring to " + host));
        }
    }
}
