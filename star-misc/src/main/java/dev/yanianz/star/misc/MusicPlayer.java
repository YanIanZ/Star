package dev.yanianz.star.misc;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import javax.annotation.Nonnull;

public final class MusicPlayer {
    private final Plugin plugin;
    private final Sound sound;
    private final Player player;
    private float volume = 1, pitch = 1;
    private boolean loop;
    private BukkitTask task;

    public MusicPlayer(@Nonnull Plugin plugin, @Nonnull Sound sound, @Nonnull Player player) {
        this.plugin = plugin; this.sound = sound; this.player = player;
    }

    @Nonnull
    public MusicPlayer volume(float v) { this.volume = v; return this; }

    @Nonnull
    public MusicPlayer pitch(float p) { this.pitch = p; return this; }

    @Nonnull
    public MusicPlayer loop() { this.loop = true; return this; }

    public void start() {
        int lengthTicks = 200;
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (!player.isOnline()) { stop(); return; }
            player.playSound(player.getLocation(), sound, SoundCategory.MASTER, volume, pitch);
        }, 0, lengthTicks);
    }

    public void stop() { if (task != null) { task.cancel(); task = null; } }

    public boolean isPlaying() { return task != null; }
}
