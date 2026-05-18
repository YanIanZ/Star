package dev.yanianz.star.vfx.shapes;

import dev.yanianz.star.vfx.ParticleShape;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import javax.annotation.Nonnull;
import java.util.List;

public final class LineShape implements ParticleShape {
    private final Particle particle;
    private final int count;
    private final Location location;
    private final double length;
    private final double offsetX, offsetY, offsetZ;
    private final double speed;
    private final Color color;
    private final List<Player> viewers;
    private final Vector direction;

    public LineShape(@Nonnull Particle particle, int count, @Nonnull Location location, double length,
                     double offsetX, double offsetY, double offsetZ, double speed,
                     Color color, @Nonnull List<Player> viewers) {
        this.particle = particle; this.count = count; this.location = location; this.length = length;
        this.offsetX = offsetX; this.offsetY = offsetY; this.offsetZ = offsetZ;
        this.speed = speed; this.color = color; this.viewers = viewers;
        this.direction = location.getDirection().normalize();
    }

    @Override
    public void play() {
        World world = location.getWorld();
        if (world == null) return;
        Particle.DustOptions options = color != null ? new Particle.DustOptions(color, 1) : null;
        for (int i = 0; i < count; i++) {
            double t = (double) i / Math.max(1, count - 1) * length;
            Location loc = location.clone().add(direction.clone().multiply(t));
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
