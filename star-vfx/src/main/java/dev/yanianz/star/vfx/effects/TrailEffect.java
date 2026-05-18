package dev.yanianz.star.vfx.effects;

import dev.yanianz.star.vfx.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import javax.annotation.Nonnull;
import java.util.List;

public final class TrailEffect implements ParticleEffect {
    private final Particle particle;
    private final int count;
    private final Entity entity;
    private final int interval;
    private final int duration;
    private final double offsetX, offsetY, offsetZ;
    private final double speed;
    private final Color color;
    private final List<Player> viewers;
    private final Plugin plugin;
    private BukkitTask task;
    private int ticksElapsed;

    public TrailEffect(@Nonnull Particle particle, int count, @Nonnull Entity entity,
                       int interval, int duration, double offsetX, double offsetY, double offsetZ,
                       double speed, Color color, @Nonnull List<Player> viewers,
                       @Nonnull Plugin plugin) {
        this.particle = particle; this.count = count; this.entity = entity;
        this.interval = interval; this.duration = duration;
        this.offsetX = offsetX; this.offsetY = offsetY; this.offsetZ = offsetZ;
        this.speed = speed; this.color = color; this.viewers = viewers;
        this.plugin = plugin;
    }

    @Override
    public void start() {
        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (ticksElapsed >= duration || entity.isDead() || !entity.isValid()) { stop(); return; }
            Location loc = entity.getLocation().add(0, 1, 0);
            spawn(loc);
            ticksElapsed += interval;
        }, 0, interval);
    }

    private void spawn(Location loc) {
        Particle.DustOptions options = color != null ? new Particle.DustOptions(color, 1) : null;
        if (viewers.isEmpty()) {
            if (options != null && particle == Particle.DUST)
                loc.getWorld().spawnParticle(particle, loc, count, offsetX, offsetY, offsetZ, speed, options);
            else loc.getWorld().spawnParticle(particle, loc, count, offsetX, offsetY, offsetZ, speed);
        } else {
            for (Player v : viewers) {
                if (options != null && particle == Particle.DUST)
                    v.spawnParticle(particle, loc, count, offsetX, offsetY, offsetZ, speed, options);
                else v.spawnParticle(particle, loc, count, offsetX, offsetY, offsetZ, speed);
            }
        }
    }

    @Override public void stop() { if (task != null) { task.cancel(); task = null; } }
    @Override public boolean isRunning() { return task != null; }
}
