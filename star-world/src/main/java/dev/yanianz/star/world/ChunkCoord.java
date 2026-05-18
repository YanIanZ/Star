package dev.yanianz.star.world;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import javax.annotation.Nonnull;

/**
 * Immutable chunk coordinate.
 * Provides conversion from chunks/locations and support for coordinate hashing for use in Sets/Maps.
 */
public record ChunkCoord(@Nonnull String worldName, int x, int z) {
    /** Creates a ChunkCoord from a Bukkit Chunk. */
    @Nonnull
    public static ChunkCoord fromChunk(@Nonnull Chunk chunk) {
        return new ChunkCoord(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    /** Creates a ChunkCoord from a block location. */
    @Nonnull
    public static ChunkCoord fromLocation(@Nonnull Location loc) {
        return new ChunkCoord(loc.getWorld().getName(), loc.getBlockX() >> 4, loc.getBlockZ() >> 4);
    }

    /** Packs two chunk coordinates into a single long key for use in collections. */
    @Nonnull
    public static long key(int x, int z) {
        return (long) x & 0xFFFFFFFFL | ((long) z & 0xFFFFFFFFL) << 32;
    }

    public long toKey() {
        return key(x, z);
    }

    /** Returns the minimum block X coordinate of this chunk. */
    public int getBlockX() { return x << 4; }
    /** Returns the minimum block Z coordinate of this chunk. */
    public int getBlockZ() { return z << 4; }
 
    /** Returns a new ChunkCoord offset by the given chunk deltas. */
    @Nonnull
    public ChunkCoord offset(int dx, int dz) {
        return new ChunkCoord(worldName, x + dx, z + dz);
    }

    @Nonnull public ChunkCoord withX(int x) { return new ChunkCoord(worldName, x, z); }
    @Nonnull public ChunkCoord withZ(int z) { return new ChunkCoord(worldName, x, z); }

    @Override
    public String toString() {
        return worldName + ":" + x + "," + z;
    }
}
