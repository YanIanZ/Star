package dev.yanianz.star.world;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Queries the world for terrain data: biomes, highest blocks, walkability checks.
 */
public final class TerrainSampler {
    private TerrainSampler() {}

    /** Returns the biome at the given location. */
    @Nonnull
    public static Biome getBiome(@Nonnull Location loc) {
        return loc.getWorld().getBiome(loc);
    }

    /** Returns the highest non-air Y coordinate at the given location. */
    public static int getHighestY(@Nonnull Location loc) {
        return loc.getWorld().getHighestBlockYAt(loc);
    }

    /** Returns the highest non-air Y coordinate at the given world coordinates. */
    public static int getHighestY(@Nonnull World world, int x, int z) {
        return world.getHighestBlockYAt(x, z);
    }

    /** Returns the highest non-air block at the given location, or null if none. */
    @Nullable
    public static Block getHighestBlock(@Nonnull Location loc) {
        return loc.getWorld().getHighestBlockAt(loc);
    }

    /** Returns true if the block at the location has a clear view of the sky above. */
    public static boolean isExposedToSky(@Nonnull Location loc) {
        World world = loc.getWorld();
        int bx = loc.getBlockX(), bz = loc.getBlockZ();
        for (int y = loc.getBlockY() + 1; y < world.getMaxHeight(); y++) {
            if (!world.getBlockAt(bx, y, bz).isEmpty()) return false;
        }
        return true;
    }

    @Nonnull
    public static Material getTypeAt(@Nonnull World world, int x, int y, int z) {
        return world.getBlockAt(x, y, z).getType();
    }

    /** Returns true if the given location has a solid block below and two air blocks above (suitable for standing). */
    public static boolean isWalkable(@Nonnull Location loc) {
        Block below = loc.clone().subtract(0, 1, 0).getBlock();
        return below.getType().isSolid() && loc.getBlock().isEmpty() && loc.clone().add(0, 1, 0).getBlock().isEmpty();
    }
}
