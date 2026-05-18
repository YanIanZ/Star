package dev.yanianz.star.test;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ItemFactory {
    private ItemFactory() {}

    @Nonnull
    public static ItemBuilder create(@Nonnull Material material) {
        return new ItemBuilder(material);
    }

    @Nonnull
    public static ItemBuilder sword(@Nonnull Material material) {
        return new ItemBuilder(material);
    }

    public static final class ItemBuilder {
        private final ItemStack item;
        private final List<String> lore = new ArrayList<>();

        ItemBuilder(Material material) { this.item = new ItemStack(material); }

        @Nonnull public ItemBuilder name(@Nonnull String name) {
            item.getItemMeta().setDisplayName(name); return this;
        }

        @Nonnull public ItemBuilder lore(@Nonnull String... lines) {
            Collections.addAll(lore, lines);
            item.getItemMeta().setLore(lore);
            return this;
        }

        @Nonnull public ItemBuilder amount(int amount) { item.setAmount(amount); return this; }

        @Nonnull public ItemStack build() { return item; }
    }
}
