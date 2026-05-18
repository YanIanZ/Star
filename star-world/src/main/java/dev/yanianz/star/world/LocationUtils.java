package dev.yanianz.star.world;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import javax.annotation.Nonnull;

public final class LocationUtils {
    private LocationUtils() {}

    public static double distance2D(@Nonnull Location a, @Nonnull Location b) {
        return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getZ() - b.getZ(), 2));
    }

    public static double distance3D(@Nonnull Location a, @Nonnull Location b) {
        return a.distance(b);
    }

    @Nonnull public static Location midpoint(@Nonnull Location a, @Nonnull Location b) {
        return a.clone().add(b).multiply(0.5);
    }

    @Nonnull public static Location randomInBox(@Nonnull World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        return new Location(world,
            minX + Math.random() * (maxX - minX + 1),
            minY + Math.random() * (maxY - minY + 1),
            minZ + Math.random() * (maxZ - minZ + 1));
    }

    @Nonnull public static Location randomInCircle(@Nonnull Location center, double radius) {
        double angle = Math.random() * 2 * Math.PI;
        double r = radius * Math.sqrt(Math.random());
        return center.clone().add(Math.cos(angle) * r, 0, Math.sin(angle) * r);
    }

    @Nonnull public static Location centerBlock(@Nonnull Location loc) {
        return new Location(loc.getWorld(), loc.getBlockX() + 0.5, loc.getBlockY(), loc.getBlockZ() + 0.5);
    }

    @Nonnull public static Vector direction(@Nonnull Location from, @Nonnull Location to) {
        return to.clone().subtract(from).toVector().normalize();
    }

    @Nonnull public static Location faceLocation(@Nonnull Location from, @Nonnull Location target) {
        from = from.clone();
        Vector dir = target.clone().subtract(from).toVector();
        from.setDirection(dir);
        return from;
    }

    public static boolean isSameBlock(@Nonnull Location a, @Nonnull Location b) {
        return a.getWorld().equals(b.getWorld()) && a.getBlockX() == b.getBlockX() && a.getBlockY() == b.getBlockY() && a.getBlockZ() == b.getBlockZ();
    }

    @Nonnull public static Location offsetXZ(@Nonnull Location loc, double offsetX, double offsetZ) {
        return loc.clone().add(loc.getDirection().getZ() * offsetX + loc.getDirection().getX() * offsetZ,
            0, -loc.getDirection().getX() * offsetX + loc.getDirection().getZ() * offsetZ);
    }
}
