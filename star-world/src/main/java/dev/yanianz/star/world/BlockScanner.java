package dev.yanianz.star.world;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * Iterates over blocks in regions (box, sphere, chunk) applying a consumer callback to each block.
 */
public final class BlockScanner {
    private BlockScanner() {}

    /** Iterates over every block in the axis-aligned box between two corner locations. */
    public static void scanBox(@Nonnull Location corner1, @Nonnull Location corner2, @Nonnull Consumer<Block> consumer) {
        World world = corner1.getWorld();
        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int minY = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int maxY = Math.max(corner1.getBlockY(), corner2.getBlockY());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());
        for (int x = minX; x <= maxX; x++)
            for (int y = minY; y <= maxY; y++)
                for (int z = minZ; z <= maxZ; z++)
                    consumer.accept(world.getBlockAt(x, y, z));
    }

    /** Iterates over every block in the given CuboidRegion. */
    public static void scanBox(@Nonnull CuboidRegion region, @Nonnull Consumer<Block> consumer) {
        World world = region.getWorld();
        for (int x = region.getMinX(); x <= region.getMaxX(); x++)
            for (int y = region.getMinY(); y <= region.getMaxY(); y++)
                for (int z = region.getMinZ(); z <= region.getMaxZ(); z++)
                    consumer.accept(world.getBlockAt(x, y, z));
    }

    /** Iterates over every block within the given radius of a center location. */
    public static void scanSphere(@Nonnull Location center, double radius, @Nonnull Consumer<Block> consumer) {
        World world = center.getWorld();
        int cx = center.getBlockX(), cy = center.getBlockY(), cz = center.getBlockZ();
        int r = (int) Math.ceil(radius);
        for (int x = cx - r; x <= cx + r; x++)
            for (int y = Math.max(cy - r, world.getMinHeight()); y <= Math.min(cy + r, world.getMaxHeight()); y++)
                for (int z = cz - r; z <= cz + r; z++)
                    if (center.distanceSquared(new Location(world, x + 0.5, y + 0.5, z + 0.5)) <= radius * radius)
                        consumer.accept(world.getBlockAt(x, y, z));
    }

    /** Iterates over every block in the chunk containing the given location. */
    public static void scanChunk(@Nonnull Location loc, @Nonnull Consumer<Block> consumer) {
        World world = loc.getWorld();
        int cx = loc.getBlockX() >> 4, cz = loc.getBlockZ() >> 4;
        int baseX = cx << 4, baseZ = cz << 4;
        for (int x = baseX; x < baseX + 16; x++)
            for (int y = world.getMinHeight(); y < world.getMaxHeight(); y++)
                for (int z = baseZ; z < baseZ + 16; z++)
                    consumer.accept(world.getBlockAt(x, y, z));
    }
}
