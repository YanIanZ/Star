package dev.yanianz.star.misc;

import org.bukkit.entity.Player;
import javax.annotation.Nonnull;

public final class ResourcePackSender {
    private ResourcePackSender() {}

    public static void send(@Nonnull Player player, @Nonnull String url, @Nonnull String hash) {
        player.setResourcePack(url, hash.getBytes());
    }

    public static void send(@Nonnull Player player, @Nonnull String url, @Nonnull String hash, boolean required, String prompt) {
        player.setResourcePack(url, hash.getBytes(), prompt, required);
    }
}
