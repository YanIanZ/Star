package dev.yanianz.star.vfx;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import javax.annotation.Nonnull;

public final class ParticleUtils {
    private ParticleUtils() {}

    public static void circle(@Nonnull Location loc, @Nonnull Particle particle, int count, double radius) {
        ParticleBuilder.shape(ShapeType.CIRCLE).at(loc).particle(particle).count(count).radius(radius).play();
    }

    public static void circle(@Nonnull Location loc, @Nonnull Particle particle, int count, double radius, @Nonnull Color color) {
        ParticleBuilder.shape(ShapeType.CIRCLE).at(loc).particle(particle).count(count).radius(radius).color(color).play();
    }

    public static void line(@Nonnull Location from, @Nonnull Location to, @Nonnull Particle particle, double spacing) {
        double dist = from.distance(to);
        int count = Math.max(2, (int) (dist / spacing));
        Location mid = from.clone();
        mid.setDirection(to.clone().subtract(from).toVector());
        ParticleBuilder.shape(ShapeType.LINE).at(mid).particle(particle).count(count).length(dist).play();
    }

    public static void sphere(@Nonnull Location loc, @Nonnull Particle particle, int count, double radius) {
        ParticleBuilder.shape(ShapeType.SPHERE).at(loc).particle(particle).count(count).radius(radius).play();
    }

    public static void explosion(@Nonnull Location loc, @Nonnull Particle particle, int count, double radius) {
        ParticleBuilder.effect(EffectType.EXPLOSION).at(loc).particle(particle).count(count).radius(radius)
            .interval(1).duration(20).build().start();
    }
}
