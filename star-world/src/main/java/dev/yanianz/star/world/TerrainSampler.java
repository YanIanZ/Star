package dev.yanianz.star.world;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class TerrainSampler {
    private TerrainSampler() {}

    @Nonnull
    public static Biome getBiome(@Nonnull Location loc) {
        return loc.getWorld().getBiome(loc);
    }

    public static int getHighestY(@Nonnull Location loc) {
        return loc.getWorld().getHighestBlockYAt(loc);
    }

    public static int getHighestY(@Nonnull World world, int x, int z) {
        return world.getHighestBlockYAt(x, z);
    }

    @Nullable
    public static Block getHighestBlock(@Nonnull Location loc) {
        return loc.getWorld().getHighestBlockAt(loc);
    }

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

    public static boolean isWalkable(@Nonnull Location loc) {
        Block below = loc.clone().subtract(0, 1, 0).getBlock();
        return below.getType().isSolid() && loc.getBlock().isEmpty() && loc.clone().add(0, 1, 0).getBlock().isEmpty();
    }
}
