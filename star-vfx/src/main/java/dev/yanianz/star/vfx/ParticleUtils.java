package dev.yanianz.star.vfx;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import javax.annotation.Nonnull;

/**
 * Static convenience methods for common particle shapes.
 * For complex configurations, use {@link ParticleBuilder} directly.
 */
public final class ParticleUtils {
    private ParticleUtils() {}

    /** Spawns a circle of particles at the given location. */
    public static void circle(@Nonnull Location loc, @Nonnull Particle particle, int count, double radius) {
        ParticleBuilder.shape(ShapeType.CIRCLE).at(loc).particle(particle).count(count).radius(radius).play();
    }

    /** Spawns a circle of colored particles at the given location. */
    public static void circle(@Nonnull Location loc, @Nonnull Particle particle, int count, double radius, @Nonnull Color color) {
        ParticleBuilder.shape(ShapeType.CIRCLE).at(loc).particle(particle).count(count).radius(radius).color(color).play();
    }

    /** Spawns a line of particles from one location to another. */
    public static void line(@Nonnull Location from, @Nonnull Location to, @Nonnull Particle particle, double spacing) {
        double dist = from.distance(to);
        int count = Math.max(2, (int) (dist / spacing));
        Location mid = from.clone();
        mid.setDirection(to.clone().subtract(from).toVector());
        ParticleBuilder.shape(ShapeType.LINE).at(mid).particle(particle).count(count).length(dist).play();
    }

    /** Spawns a sphere of particles at the given location. */
    public static void sphere(@Nonnull Location loc, @Nonnull Particle particle, int count, double radius) {
        ParticleBuilder.shape(ShapeType.SPHERE).at(loc).particle(particle).count(count).radius(radius).play();
    }

    /** Creates and starts an animated explosion effect at the given location. */
    public static void explosion(@Nonnull Location loc, @Nonnull Particle particle, int count, double radius) {
        ParticleBuilder.effect(EffectType.EXPLOSION).at(loc).particle(particle).count(count).radius(radius)
            .interval(1).duration(20).build().start();
    }
}
