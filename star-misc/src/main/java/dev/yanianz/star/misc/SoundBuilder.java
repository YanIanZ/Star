package dev.yanianz.star.misc;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import javax.annotation.Nonnull;

public final class SoundBuilder {
    private final Sound sound;
    private Player player;
    private float pitch = 1, volume = 1;
    private SoundCategory category = SoundCategory.MASTER;

    private SoundBuilder(@Nonnull Sound sound) { this.sound = sound; }

    @Nonnull
    public static SoundBuilder play(@Nonnull Sound sound) { return new SoundBuilder(sound); }

    @Nonnull
    public SoundBuilder to(@Nonnull Player player) { this.player = player; return this; }

    @Nonnull
    public SoundBuilder pitch(float pitch) { this.pitch = pitch; return this; }

    @Nonnull
    public SoundBuilder volume(float volume) { this.volume = volume; return this; }

    @Nonnull
    public SoundBuilder category(@Nonnull SoundCategory cat) { this.category = cat; return this; }

    public void play() {
        if (player != null) player.playSound(player.getLocation(), sound, category, volume, pitch);
    }
}
