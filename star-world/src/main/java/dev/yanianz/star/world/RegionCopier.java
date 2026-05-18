package dev.yanianz.star.world;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import javax.annotation.Nonnull;

/**
 * Copies blocks and block data from one region to another location.
 */
public final class RegionCopier {
    private RegionCopier() {}

    @Nonnull
    public static CopyResult copy(@Nonnull Location pos1, @Nonnull Location pos2, @Nonnull Location to) {
        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        int dx = to.getBlockX() - minX;
        int dy = to.getBlockY() - minY;
        int dz = to.getBlockZ() - minZ;

        BlockState[] states = new BlockState[(maxX-minX+1)*(maxY-minY+1)*(maxZ-minZ+1)];
        int idx = 0;
        for (int x = minX; x <= maxX; x++)
            for (int y = minY; y <= maxY; y++)
                for (int z = minZ; z <= maxZ; z++)
                    states[idx++] = pos1.getWorld().getBlockAt(x, y, z).getState();

        for (BlockState state : states) {
            Block target = to.getWorld().getBlockAt(state.getX() + dx, state.getY() + dy, state.getZ() + dz);
            target.setType(state.getType());
            target.setBlockData(state.getBlockData());
        }

        return new CopyResult(minX, maxX, minY, maxY, minZ, maxZ, dx, dy, dz, states.length);
    }

    public record CopyResult(int minX, int maxX, int minY, int maxY, int minZ, int maxZ, int dx, int dy, int dz, int blocks) {}
}
