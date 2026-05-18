package dev.yanianz.star.gui.animation;

import dev.yanianz.star.gui.Gui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import java.util.List;

public final class SlotAnimation implements GuiAnimation {

    private final int slot;
    private final List<ItemStack> frames;
    private final long intervalTicks;
    private final Plugin plugin;
    private BukkitTask task;
    private int currentFrame = 0;

    public SlotAnimation(@Nonnull Plugin plugin, int slot, @Nonnull List<ItemStack> frames, long intervalTicks) {
        if (frames.isEmpty()) {
            throw new IllegalArgumentException("frames must not be empty");
        }
        this.plugin = plugin;
        this.slot = slot;
        this.frames = frames;
        this.intervalTicks = intervalTicks;
    }

    @Override
    public void start(@Nonnull Gui gui, @Nonnull Player player) {
        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            currentFrame = (currentFrame + 1) % frames.size();
            gui.setItem(slot, frames.get(currentFrame).clone(), null);
        }, 0, intervalTicks);
    }

    @Override
    public void stop(@Nonnull Gui gui, @Nonnull Player player) {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    @Override
    public boolean isRunning() {
        return task != null;
    }
}
