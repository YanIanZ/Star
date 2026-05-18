package dev.yanianz.star.world;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import javax.annotation.Nonnull;

public record ChunkCoord(@Nonnull String worldName, int x, int z) {
    @Nonnull
    public static ChunkCoord fromChunk(@Nonnull Chunk chunk) {
        return new ChunkCoord(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    @Nonnull
    public static ChunkCoord fromLocation(@Nonnull Location loc) {
        return new ChunkCoord(loc.getWorld().getName(), loc.getBlockX() >> 4, loc.getBlockZ() >> 4);
    }

    @Nonnull
    public static long key(int x, int z) {
        return (long) x & 0xFFFFFFFFL | ((long) z & 0xFFFFFFFFL) << 32;
    }

    public long toKey() {
        return key(x, z);
    }

    public int getBlockX() { return x << 4; }
    public int getBlockZ() { return z << 4; }

    @Nonnull
    public ChunkCoord offset(int dx, int dz) {
        return new ChunkCoord(worldName, x + dx, z + dz);
    }

    @Override
    public String toString() {
        return worldName + ":" + x + "," + z;
    }
}
