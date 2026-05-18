package dev.yanianz.star.profiles;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public final class ProfileInventory {
    private ProfileInventory() {}

    @Nonnull public static List<ItemStack> snapshot(@Nonnull Player player) {
        return Arrays.asList(player.getInventory().getContents());
    }

    public static void restore(@Nonnull Player player, @Nonnull List<ItemStack> items) {
        player.getInventory().setContents(items.toArray(new ItemStack[0]));
    }
}
