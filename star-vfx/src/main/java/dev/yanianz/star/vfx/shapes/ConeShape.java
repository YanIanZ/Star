package dev.yanianz.star.vfx.shapes;

import dev.yanianz.star.vfx.ParticleShape;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import javax.annotation.Nonnull;
import java.util.List;

public final class ConeShape implements ParticleShape {
    private final Particle particle;
    private final int count;
    private final Location location;
    private final double radius;
    private final double height;
    private final double offsetX, offsetY, offsetZ;
    private final double speed;
    private final Color color;
    private final List<Player> viewers;

    public ConeShape(@Nonnull Particle particle, int count, @Nonnull Location location, double radius,
                     double height, double offsetX, double offsetY, double offsetZ, double speed,
                     Color color, @Nonnull List<Player> viewers) {
        this.particle = particle; this.count = count; this.location = location;
        this.radius = radius; this.height = height;
        this.offsetX = offsetX; this.offsetY = offsetY; this.offsetZ = offsetZ;
        this.speed = speed; this.color = color; this.viewers = viewers;
    }

    @Override
    public void play() {
        World world = location.getWorld();
        if (world == null) return;
        Particle.DustOptions options = color != null ? new Particle.DustOptions(color, 1) : null;
        int perLevel = Math.max(1, count / 10);
        for (int i = 0; i <= perLevel; i++) {
            double t = (double) i / perLevel;
            double y = t * height;
            double r = radius * (1 - t);
            int ringCount = Math.max(4, count / 10);
            for (int j = 0; j < ringCount; j++) {
                double angle = 2 * Math.PI * j / ringCount;
                Location loc = location.clone().add(Math.cos(angle) * r, y, Math.sin(angle) * r);
                spawn(world, loc, options);
            }
        }
    }

    private void spawn(World world, Location loc, Particle.DustOptions options) {
        if (viewers.isEmpty()) {
            if (options != null && particle == Particle.DUST)
                world.spawnParticle(particle, loc, 1, offsetX, offsetY, offsetZ, speed, options);
            else world.spawnParticle(particle, loc, 1, offsetX, offsetY, offsetZ, speed);
        } else {
            for (Player v : viewers) {
                if (options != null && particle == Particle.DUST)
                    v.spawnParticle(particle, loc, 1, offsetX, offsetY, offsetZ, speed, options);
                else v.spawnParticle(particle, loc, 1, offsetX, offsetY, offsetZ, speed);
            }
        }
    }

    @Override @Nonnull public Particle getParticle() { return particle; }
    @Override public int getCount() { return count; }
    @Override @Nonnull public Location getLocation() { return location; }
}
