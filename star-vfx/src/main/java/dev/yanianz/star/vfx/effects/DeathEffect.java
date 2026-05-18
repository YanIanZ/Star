package dev.yanianz.star.vfx.effects;

import dev.yanianz.star.vfx.ParticleEffect;
import dev.yanianz.star.vfx.shapes.CircleShape;
import dev.yanianz.star.vfx.shapes.HelixShape;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import javax.annotation.Nonnull;
import java.util.List;

public final class DeathEffect implements ParticleEffect {
    private final Particle particle;
    private final int count;
    private final Location location;
    private final double radius;
    private final double height;
    private final double offsetX, offsetY, offsetZ;
    private final double speed;
    private final Color color;
    private final List<Player> viewers;
    private final Plugin plugin;
    private BukkitTask task;

    public DeathEffect(@Nonnull Particle particle, int count, @Nonnull Location location,
                       double radius, double height, double offsetX, double offsetY, double offsetZ,
                       double speed, Color color, @Nonnull List<Player> viewers,
                       @Nonnull Plugin plugin) {
        this.particle = particle; this.count = count; this.location = location;
        this.radius = radius; this.height = height;
        this.offsetX = offsetX; this.offsetY = offsetY; this.offsetZ = offsetZ;
        this.speed = speed; this.color = color; this.viewers = viewers;
        this.plugin = plugin;
    }

    @Override
    public void start() {
        // Immediate burst rings
        new CircleShape(particle, count, location, radius, offsetX, offsetY, offsetZ, speed, color, viewers).play();
        new CircleShape(particle, count / 2, location, radius * 0.5, offsetX, offsetY, offsetZ, speed, color, viewers).play();
        // Rising helix animation
        task = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            int tick = 0;
            @Override public void run() {
                if (tick >= 10) { stop(); return; }
                double progress = tick / 10.0;
                double currentRadius = radius * (1 - progress);
                new HelixShape(particle, count / 2, location.clone().add(0, progress * height, 0),
                    currentRadius, height * 0.3, 2, offsetX, offsetY, offsetZ, speed, color,
                    viewers).play();
                tick++;
            }
        }, 0, 2);
    }

    @Override public void stop() { if (task != null) { task.cancel(); task = null; } }
    @Override public boolean isRunning() { return task != null; }
}
