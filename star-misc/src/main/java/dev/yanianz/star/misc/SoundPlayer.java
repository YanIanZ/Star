package dev.yanianz.star.misc;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;
import javax.annotation.Nonnull;

public final class SoundPlayer {
    private SoundPlayer() {}

    public static void play(@Nonnull Player player, @Nonnull Sound sound, float volume, float pitch) {
        player.playSound(player.getLocation(), sound, SoundCategory.MASTER, volume, pitch);
    }

    public static void play(@Nonnull Player player, @Nonnull Sound sound) { play(player, sound, 1f, 1f); }

    public static void broadcast(@Nonnull World world, @Nonnull Sound sound, float volume, float pitch) {
        for (Player p : world.getPlayers()) p.playSound(p.getLocation(), sound, SoundCategory.MASTER, volume, pitch);
    }
}
