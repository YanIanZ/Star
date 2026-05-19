package dev.yanianz.star.vfx.effects;

import dev.yanianz.star.vfx.ParticleEffect;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import javax.annotation.Nonnull;
import java.util.List;

public final class SpiralEffect implements ParticleEffect {
    private final Particle particle; private final int count; private final Location location;
    private final double radius; private final double height; private final int rotations;
    private final int interval; private final int duration;
    private final double offsetX, offsetY, offsetZ; private final double speed;
    private final Color color; private final List<Player> viewers; private final Plugin plugin;
    private BukkitTask task; private int ticksElapsed;

    public SpiralEffect(@Nonnull Plugin plugin, @Nonnull Particle particle, int count, @Nonnull Location location, double radius, double height, int rotations, int interval, int duration, double offsetX, double offsetY, double offsetZ, double speed, Color color, @Nonnull List<Player> viewers) {
        this.plugin = plugin; this.particle = particle; this.count = count; this.location = location;
        this.radius = radius; this.height = height; this.rotations = rotations;
        this.interval = interval; this.duration = duration;
        this.offsetX = offsetX; this.offsetY = offsetY; this.offsetZ = offsetZ; this.speed = speed; this.color = color; this.viewers = viewers;
    }

    @Override public void start() {
        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (ticksElapsed >= duration) { stop(); return; }
            double progress = (double) ticksElapsed / duration;
            double angle = 2 * Math.PI * rotations * progress;
            double y = progress * height;
            Location loc = location.clone().add(Math.cos(angle) * radius, y, Math.sin(angle) * radius);
            Particle.DustOptions options = color != null ? new Particle.DustOptions(color, 1) : null;
            for (int i = 0; i < count; i++) { spawn(loc, options); }
            ticksElapsed += interval;
        }, 0, interval);
    }

    private void spawn(Location loc, Particle.DustOptions options) {
        if (viewers.isEmpty()) {
            if (options != null && particle == Particle.DUST) loc.getWorld().spawnParticle(particle, loc, 1, offsetX, offsetY, offsetZ, speed, options);
            else loc.getWorld().spawnParticle(particle, loc, 1, offsetX, offsetY, offsetZ, speed);
        } else { for (Player v : viewers) { if (options != null && particle == Particle.DUST) v.spawnParticle(particle, loc, 1, offsetX, offsetY, offsetZ, speed, options); else v.spawnParticle(particle, loc, 1, offsetX, offsetY, offsetZ, speed); } }
    }

    @Override public void stop() { if (task != null) { task.cancel(); task = null; } }
    @Override public boolean isRunning() { return task != null; }
}
