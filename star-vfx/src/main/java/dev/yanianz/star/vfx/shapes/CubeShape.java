package dev.yanianz.star.vfx.shapes;

import dev.yanianz.star.vfx.ParticleShape;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import javax.annotation.Nonnull;
import java.util.List;

public final class CubeShape implements ParticleShape {
    private final Particle particle;
    private final int count;
    private final Location location;
    private final double radius;
    private final double offsetX, offsetY, offsetZ;
    private final double speed;
    private final Color color;
    private final List<Player> viewers;

    public CubeShape(@Nonnull Particle particle, int count, @Nonnull Location location, double radius,
                     double offsetX, double offsetY, double offsetZ, double speed,
                     Color color, @Nonnull List<Player> viewers) {
        this.particle = particle; this.count = count; this.location = location; this.radius = radius;
        this.offsetX = offsetX; this.offsetY = offsetY; this.offsetZ = offsetZ;
        this.speed = speed; this.color = color; this.viewers = viewers;
    }

    @Override
    public void play() {
        World world = location.getWorld();
        if (world == null) return;
        Particle.DustOptions options = color != null ? new Particle.DustOptions(color, 1) : null;
        double r = radius;
        double[][] corners = {{-r,-r,-r},{r,-r,-r},{r,r,-r},{-r,r,-r},{-r,-r,r},{r,-r,r},{r,r,r},{-r,r,r}};
        int[][] edges = {{0,1},{1,2},{2,3},{3,0},{4,5},{5,6},{6,7},{7,4},{0,4},{1,5},{2,6},{3,7}};
        int perEdge = Math.max(1, count / 12);
        for (int[] edge : edges) {
            double[] a = corners[edge[0]], b = corners[edge[1]];
            for (int i = 0; i < perEdge; i++) {
                double t = (double) i / Math.max(1, perEdge - 1);
                Location loc = location.clone().add(a[0]+(b[0]-a[0])*t, a[1]+(b[1]-a[1])*t, a[2]+(b[2]-a[2])*t);
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
