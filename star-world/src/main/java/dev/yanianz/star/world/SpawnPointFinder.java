package dev.yanianz.star.world;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class SpawnPointFinder {
    private SpawnPointFinder() {}

    @Nullable
    public static Location findSafe(@Nonnull Player player, double radius, int attempts) {
        World world = player.getWorld();
        Location origin = player.getLocation();
        for (int i = 0; i < attempts; i++) {
            Location loc = LocationUtils.randomInCircle(origin, radius);
            loc.setY(world.getHighestBlockYAt(loc) + 1);
            if (isSafe(loc)) return loc;
        }
        return null;
    }

    @Nullable
    public static Location findSafe(@Nonnull Location origin, double radius, int attempts) {
        World world = origin.getWorld();
        for (int i = 0; i < attempts; i++) {
            Location loc = LocationUtils.randomInCircle(origin, radius);
            loc.setY(world.getHighestBlockYAt(loc) + 1);
            if (isSafe(loc)) return loc;
        }
        return null;
    }

    public static boolean isSafe(@Nonnull Location loc) {
        return TerrainSampler.isWalkable(loc);
    }
}
