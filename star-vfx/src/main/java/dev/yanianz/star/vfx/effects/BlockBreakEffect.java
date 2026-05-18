package dev.yanianz.star.vfx.effects;

import dev.yanianz.star.vfx.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import javax.annotation.Nonnull;
import java.util.List;

public final class BlockBreakEffect implements ParticleEffect {
    private final Particle particle;
    private final int count;
    private final Location location;
    private final double radius;
    private final double offsetX, offsetY, offsetZ;
    private final double speed;
    private final Color color;
    private final List<Player> viewers;
    private BukkitTask task;

    public BlockBreakEffect(@Nonnull Particle particle, int count, @Nonnull Location location,
                            double radius, double offsetX, double offsetY, double offsetZ,
                            double speed, Color color, @Nonnull List<Player> viewers) {
        this.particle = particle; this.count = count; this.location = location;
        this.radius = radius;
        this.offsetX = offsetX; this.offsetY = offsetY; this.offsetZ = offsetZ;
        this.speed = speed; this.color = color; this.viewers = viewers;
    }

    @Override
    public void start() {
        Plugin plugin = Bukkit.getPluginManager().getPlugins().length > 0
            ? Bukkit.getPluginManager().getPlugins()[0] : null;
        if (plugin == null) return;
        Material material = location.getBlock().getType();
        if (material.isAir()) material = Material.STONE;
        BlockData blockData = material.createBlockData();
        task = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            int tick = 0;
            @Override public void run() {
                if (tick >= 5) { stop(); return; }
                for (int i = 0; i < count / 5; i++) {
                    Location loc = location.clone().add(
                        (Math.random() - 0.5) * radius * 2,
                        Math.random() * radius,
                        (Math.random() - 0.5) * radius * 2);
                    if (viewers.isEmpty()) {
                        loc.getWorld().spawnParticle(Particle.BLOCK, loc, 1, offsetX, offsetY, offsetZ, speed, blockData);
                    } else {
                        for (Player v : viewers)
                            v.spawnParticle(Particle.BLOCK, loc, 1, offsetX, offsetY, offsetZ, speed, blockData);
                    }
                }
                tick++;
            }
        }, 0, 2);
    }

    @Override public void stop() { if (task != null) { task.cancel(); task = null; } }
    @Override public boolean isRunning() { return task != null; }
}
