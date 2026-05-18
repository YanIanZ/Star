package dev.yanianz.star.vfx.shapes;

import dev.yanianz.star.vfx.ParticleShape;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import javax.annotation.Nonnull;
import java.util.List;

public final class StarShape implements ParticleShape {
    private final Particle particle;
    private final int count;
    private final Location location;
    private final double radius;
    private final int points;
    private final double startAngle;
    private final double offsetX, offsetY, offsetZ;
    private final double speed;
    private final Color color;
    private final List<Player> viewers;

    public StarShape(@Nonnull Particle particle, int count, @Nonnull Location location, double radius,
                     int points, double startAngle, double offsetX, double offsetY, double offsetZ,
                     double speed, Color color, @Nonnull List<Player> viewers) {
        this.particle = particle; this.count = count; this.location = location;
        this.radius = radius; this.points = points; this.startAngle = startAngle;
        this.offsetX = offsetX; this.offsetY = offsetY; this.offsetZ = offsetZ;
        this.speed = speed; this.color = color; this.viewers = viewers;
    }

    @Override
    public void play() {
        World world = location.getWorld();
        if (world == null) return;
        Particle.DustOptions options = color != null ? new Particle.DustOptions(color, 1) : null;
        double innerRadius = radius * 0.4;
        for (int i = 0; i < count; i++) {
            double t = (double) i / Math.max(1, count - 1) * points * 2 * Math.PI;
            double angle = startAngle + t;
            int segment = (int) (i * points * 2.0 / count);
            double r = (segment % 2 == 0) ? radius : innerRadius;
            Location loc = location.clone().add(Math.cos(angle) * r, 0, Math.sin(angle) * r);
            spawn(world, loc, options);
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
