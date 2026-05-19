package dev.yanianz.star.misc;

import org.bukkit.NamespacedKey;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import javax.annotation.Nonnull;

public final class CustomSound {
    private CustomSound() {}

    public static void play(@Nonnull Plugin plugin, @Nonnull Player player, @Nonnull String soundKey, float volume, float pitch) {
        NamespacedKey key = new NamespacedKey(plugin, soundKey);
        player.playSound(player.getLocation(), key.getKey(), SoundCategory.MASTER, volume, pitch);
    }
}
