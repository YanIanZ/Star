package dev.yanianz.star.world;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import javax.annotation.Nonnull;
import java.util.function.Predicate;

public final class BlockProcessor {
    private BlockProcessor() {}

    public static int fillBox(@Nonnull Location c1, @Nonnull Location c2, @Nonnull Material material) {
        int[] count = {0};
        BlockScanner.scanBox(c1, c2, b -> { b.setType(material); count[0]++; });
        return count[0];
    }

    public static int replaceBox(@Nonnull Location c1, @Nonnull Location c2, @Nonnull Material from, @Nonnull Material to) {
        int[] count = {0};
        BlockScanner.scanBox(c1, c2, b -> { if (b.getType() == from) { b.setType(to); count[0]++; } });
        return count[0];
    }

    public static int replaceMatching(@Nonnull Location c1, @Nonnull Location c2, @Nonnull Predicate<Block> filter, @Nonnull Material to) {
        int[] count = {0};
        BlockScanner.scanBox(c1, c2, b -> { if (filter.test(b)) { b.setType(to); count[0]++; } });
        return count[0];
    }

    public static int countMatching(@Nonnull Location c1, @Nonnull Location c2, @Nonnull Predicate<Block> filter) {
        int[] count = {0};
        BlockScanner.scanBox(c1, c2, b -> { if (filter.test(b)) count[0]++; });
        return count[0];
    }
}
