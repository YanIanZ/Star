package dev.yanianz.star.world;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import javax.annotation.Nonnull;

/**
 * Utility methods for location math: distance, midpoint, random positions, direction vectors.
 */
public final class LocationUtils {
    private LocationUtils() {}

    /** Returns the horizontal distance (XZ plane) between two locations. */
    public static double distance2D(@Nonnull Location a, @Nonnull Location b) {
        return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getZ() - b.getZ(), 2));
    }

    /** Returns the 3D distance between two locations. */
    public static double distance3D(@Nonnull Location a, @Nonnull Location b) {
        return a.distance(b);
    }

    /** Returns the midpoint between two locations. */
    @Nonnull public static Location midpoint(@Nonnull Location a, @Nonnull Location b) {
        return a.clone().add(b).multiply(0.5);
    }

    /** Returns a random location within the given axis-aligned box boundaries. */
    @Nonnull public static Location randomInBox(@Nonnull World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        return new Location(world,
            minX + Math.random() * (maxX - minX + 1),
            minY + Math.random() * (maxY - minY + 1),
            minZ + Math.random() * (maxZ - minZ + 1));
    }

    /** Returns a random XZ location within a circle of the given radius around the center. */
    @Nonnull public static Location randomInCircle(@Nonnull Location center, double radius) {
        double angle = Math.random() * 2 * Math.PI;
        double r = radius * Math.sqrt(Math.random());
        return center.clone().add(Math.cos(angle) * r, 0, Math.sin(angle) * r);
    }

    /** Centers the location within its block (adds 0.5 to X and Z). */
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

    /** Returns true if both locations are in the same block. */
    public static boolean isSameBlock(@Nonnull Location a, @Nonnull Location b) {
        return a.getWorld().equals(b.getWorld()) && a.getBlockX() == b.getBlockX() && a.getBlockY() == b.getBlockY() && a.getBlockZ() == b.getBlockZ();
    }

    @Nonnull public static Location offsetXZ(@Nonnull Location loc, double offsetX, double offsetZ) {
        return loc.clone().add(loc.getDirection().getZ() * offsetX + loc.getDirection().getX() * offsetZ,
            0, -loc.getDirection().getX() * offsetX + loc.getDirection().getZ() * offsetZ);
    }
}
