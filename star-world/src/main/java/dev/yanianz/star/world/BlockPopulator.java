package dev.yanianz.star.world;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import javax.annotation.Nonnull;

public final class BlockPopulator {
    private BlockPopulator() {}

    public static int populateOreVein(@Nonnull Location center, @Nonnull Material oreType, double radius, int maxBlocks) {
        World world = center.getWorld();
        int[] placed = {0};
        BlockScanner.scanSphere(center, radius, block -> {
            if (placed[0] >= maxBlocks) return;
            if (block.getType() == Material.STONE || block.getType() == Material.DEEPSLATE) {
                if (Math.random() < 0.7) { block.setType(oreType); placed[0]++; }
            }
        });
        return placed[0];
    }

    public static void populateTree(@Nonnull Location base, @Nonnull Material logMaterial, @Nonnull Material leafMaterial, int height) {
        World world = base.getWorld();
        int bx = base.getBlockX(), by = base.getBlockY(), bz = base.getBlockZ();
        for (int y = 0; y < height; y++) world.getBlockAt(bx, by + y, bz).setType(logMaterial);
        for (int y = height - 2; y <= height; y++) {
            int radius = y >= height - 1 ? 1 : 2;
            for (int x = -radius; x <= radius; x++)
                for (int z = -radius; z <= radius; z++)
                    if (!(x == 0 && z == 0 && y < height))
                        world.getBlockAt(bx + x, by + y, bz + z).setType(leafMaterial);
        }
    }

    public static void populateFlower(@Nonnull Location ground) {
        Block above = ground.clone().add(0, 1, 0).getBlock();
        if (above.isEmpty() && ground.getBlock().getType().isSolid()) {
            above.setType(Math.random() < 0.5 ? Material.DANDELION : Material.POPPY);
        }
    }
}
