package dev.yanianz.star.vfx.shapes;

import dev.yanianz.star.vfx.ParticleShape;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import javax.annotation.Nonnull;
import java.util.List;

public final class RainbowShape implements ParticleShape {
    private final Particle particle;
    private final int count;
    private final Location location;
    private final double radius;
    private final double startAngle;
    private final double endAngle;
    private final double offsetX, offsetY, offsetZ;
    private final double speed;
    private final Color color;
    private final List<Player> viewers;

    public RainbowShape(@Nonnull Particle particle, int count, @Nonnull Location location, double radius,
                        double startAngle, double endAngle, double offsetX, double offsetY, double offsetZ,
                        double speed, Color color, @Nonnull List<Player> viewers) {
        this.particle = particle; this.count = count; this.location = location;
        this.radius = radius; this.startAngle = startAngle; this.endAngle = endAngle;
        this.offsetX = offsetX; this.offsetY = offsetY; this.offsetZ = offsetZ;
        this.speed = speed; this.color = color; this.viewers = viewers;
    }

    @Override
    public void play() {
        World world = location.getWorld();
        if (world == null) return;
        for (int i = 0; i < count; i++) {
            double t = (double) i / Math.max(1, count - 1);
            double angle = startAngle + (endAngle - startAngle) * t;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            Location loc = location.clone().add(x, 0, z);
            Color cycleColor = Color.fromRGB(
                (int)(Math.sin(t * 2 * Math.PI) * 127 + 128),
                (int)(Math.sin((t + 0.33) * 2 * Math.PI) * 127 + 128),
                (int)(Math.sin((t + 0.67) * 2 * Math.PI) * 127 + 128));
            Particle.DustOptions dustOptions = new Particle.DustOptions(cycleColor, 1);
            if (viewers.isEmpty()) {
                world.spawnParticle(Particle.DUST, loc, 1, offsetX, offsetY, offsetZ, speed, dustOptions);
            } else {
                for (Player v : viewers)
                    v.spawnParticle(Particle.DUST, loc, 1, offsetX, offsetY, offsetZ, speed, dustOptions);
            }
        }
    }

    @Override @Nonnull public Particle getParticle() { return particle; }
    @Override public int getCount() { return count; }
    @Override @Nonnull public Location getLocation() { return location; }
}
