package dev.yanianz.star.misc;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import javax.annotation.Nonnull;

public final class NoteBlockPlayer {
    private NoteBlockPlayer() {}

    public static void playNote(@Nonnull Player player, @Nonnull Instrument instrument, int note, float volume) {
        player.playNote(player.getLocation(), instrument, new org.bukkit.Note(note));
    }

    public static void playNote(@Nonnull Location loc, @Nonnull Instrument instrument, int note) {
        for (Player p : loc.getWorld().getPlayers()) p.playNote(loc, instrument, new org.bukkit.Note(note));
    }

    public static void playMelody(@Nonnull Player player, @Nonnull Instrument instrument, @Nonnull int[] notes, @Nonnull Plugin plugin) {
        for (int i = 0; i < notes.length; i++) {
            final int note = notes[i];
            Bukkit.getScheduler().runTaskLater(plugin, () -> player.playNote(player.getLocation(), instrument, new org.bukkit.Note(note)), i * 4L);
        }
    }

    public static void playTone(@Nonnull Player player, float pitch) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.PLAYERS, 1f, pitch);
    }
}
