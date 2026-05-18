package dev.yanianz.star.gui.animation;

import dev.yanianz.star.gui.Gui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import java.util.List;

public final class FrameAnimation implements GuiAnimation {

    private final List<List<ItemStack>> frames;
    private final long intervalTicks;
    private final Plugin plugin;
    private BukkitTask task;
    private int currentFrame = 0;

    public FrameAnimation(@Nonnull Plugin plugin, @Nonnull List<ItemStack[]> frames, long intervalTicks) {
        if (frames.isEmpty()) {
            throw new IllegalArgumentException("frames must not be empty");
        }
        this.plugin = plugin;
        this.intervalTicks = intervalTicks;
        this.frames = frames.stream().map(List::of).toList();
    }

    @Override
    public void start(@Nonnull Gui gui, @Nonnull Player player) {
        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            currentFrame = (currentFrame + 1) % frames.size();
            Inventory inv = gui.getInventory();
            List<ItemStack> frame = frames.get(currentFrame);
            for (int i = 0; i < Math.min(frame.size(), inv.getSize()); i++) {
                ItemStack item = frame.get(i);
                inv.setItem(i, item != null ? item.clone() : null);
            }
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
