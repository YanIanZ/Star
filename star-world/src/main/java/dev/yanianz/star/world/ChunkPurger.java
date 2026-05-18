package dev.yanianz.star.world;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Enumerates and processes chunks within a region.
 */
public final class ChunkPurger {
    private ChunkPurger() {}

    /** Applies a consumer to each loaded chunk in the region. Returns the number of chunks processed. */
    public static int purgeRegion(@Nonnull Location pos1, @Nonnull Location pos2, @Nonnull Consumer<Chunk> perChunk) {
        World world = pos1.getWorld();
        int minCX = Math.min(pos1.getBlockX(), pos2.getBlockX()) >> 4;
        int maxCX = Math.max(pos1.getBlockX(), pos2.getBlockX()) >> 4;
        int minCZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ()) >> 4;
        int maxCZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ()) >> 4;
        Set<Long> seen = new HashSet<>();
        int count = 0;
        for (int cx = minCX; cx <= maxCX; cx++)
            for (int cz = minCZ; cz <= maxCZ; cz++) {
                long key = ChunkCoord.key(cx, cz);
                if (seen.add(key)) {
                    Chunk chunk = world.getChunkAt(cx, cz);
                    if (chunk.isLoaded()) {
                        perChunk.accept(chunk);
                        count++;
                    }
                }
            }
        return count;
    }

    /** Returns the set of ChunkCoord entries that overlap the given region. */
    @Nonnull
    public static Set<ChunkCoord> getChunksInRegion(@Nonnull Location pos1, @Nonnull Location pos2) {
        int minCX = Math.min(pos1.getBlockX(), pos2.getBlockX()) >> 4;
        int maxCX = Math.max(pos1.getBlockX(), pos2.getBlockX()) >> 4;
        int minCZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ()) >> 4;
        int maxCZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ()) >> 4;
        Set<ChunkCoord> coords = new HashSet<>();
        for (int cx = minCX; cx <= maxCX; cx++)
            for (int cz = minCZ; cz <= maxCZ; cz++)
                coords.add(new ChunkCoord(pos1.getWorld().getName(), cx, cz));
        return coords;
    }
}
