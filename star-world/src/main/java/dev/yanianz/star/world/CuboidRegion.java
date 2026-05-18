package dev.yanianz.star.world;

import org.bukkit.Location;
import org.bukkit.World;
import javax.annotation.Nonnull;

/**
 * Represents a 3D box region between two corners.
 * Provides dimension, volume, center, and containment queries.
 */
public final class CuboidRegion {
    private final World world;
    private final int minX, minY, minZ, maxX, maxY, maxZ;

    public CuboidRegion(@Nonnull Location loc1, @Nonnull Location loc2) {
        this.world = loc1.getWorld();
        this.minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        this.minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        this.minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        this.maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        this.maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
        this.maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
    }

    @Nonnull public World getWorld() { return world; }
    public int getMinX() { return minX; }
    public int getMinY() { return minY; }
    public int getMinZ() { return minZ; }
    public int getMaxX() { return maxX; }
    public int getMaxY() { return maxY; }
    public int getMaxZ() { return maxZ; }
    /** Returns the X-axis dimension (block count) of this region. */
    public int getWidth() { return maxX - minX + 1; }
    /** Returns the Y-axis dimension (block count) of this region. */
    public int getHeight() { return maxY - minY + 1; }
    /** Returns the Z-axis dimension (block count) of this region. */
    public int getLength() { return maxZ - minZ + 1; }
    /** Returns the total number of blocks in this region. */
    public int getVolume() { return getWidth() * getHeight() * getLength(); }
    /** Returns the center location of this region. */
    @Nonnull public Location getCenter() { return new Location(world, (minX+maxX)/2.0 + 0.5, (minY+maxY)/2.0, (minZ+maxZ)/2.0 + 0.5); }
    @Nonnull public Location getMin() { return new Location(world, minX, minY, minZ); }
    @Nonnull public Location getMax() { return new Location(world, maxX, maxY, maxZ); }
    /** Returns true if the given block coordinates are inside this region. */
    public boolean contains(int x, int y, int z) { return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ; }
    /** Returns true if the given location is inside this region. */
    public boolean contains(@Nonnull Location loc) { return world.equals(loc.getWorld()) && contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()); }
}
