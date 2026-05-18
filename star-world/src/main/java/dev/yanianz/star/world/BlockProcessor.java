package dev.yanianz.star.world;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import javax.annotation.Nonnull;
import java.util.function.Predicate;

/**
 * Applies bulk operations (fill, replace, count) to blocks within regions.
 */
public final class BlockProcessor {
    private BlockProcessor() {}

    /** Fills every block in the axis-aligned box between two corners with the given material. Returns number of blocks changed. */
    public static int fillBox(@Nonnull Location c1, @Nonnull Location c2, @Nonnull Material material) {
        int[] count = {0};
        BlockScanner.scanBox(c1, c2, b -> { b.setType(material); count[0]++; });
        return count[0];
    }

    /** Replaces blocks matching the from material with the to material in the box. Returns count of replaced blocks. */
    public static int replaceBox(@Nonnull Location c1, @Nonnull Location c2, @Nonnull Material from, @Nonnull Material to) {
        int[] count = {0};
        BlockScanner.scanBox(c1, c2, b -> { if (b.getType() == from) { b.setType(to); count[0]++; } });
        return count[0];
    }

    /** Replaces blocks matching the filter predicate with the given material. Returns count of replaced blocks. */
    public static int replaceMatching(@Nonnull Location c1, @Nonnull Location c2, @Nonnull Predicate<Block> filter, @Nonnull Material to) {
        int[] count = {0};
        BlockScanner.scanBox(c1, c2, b -> { if (filter.test(b)) { b.setType(to); count[0]++; } });
        return count[0];
    }

    /** Counts blocks matching the filter predicate within the box. */
    public static int countMatching(@Nonnull Location c1, @Nonnull Location c2, @Nonnull Predicate<Block> filter) {
        int[] count = {0};
        BlockScanner.scanBox(c1, c2, b -> { if (filter.test(b)) count[0]++; });
        return count[0];
    }
}
