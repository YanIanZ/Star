package dev.yanianz.star.test;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import javax.annotation.Nonnull;

public final class InventoryFactory {
    private InventoryFactory() {}

    @Nonnull
    public static InventoryBuilder create(int rows, @Nonnull String title) {
        return new InventoryBuilder(rows, title);
    }

    public static final class InventoryBuilder {
        private final Inventory inventory;

        InventoryBuilder(int rows, String title) {
            this.inventory = Bukkit.createInventory(null, rows * 9, title);
        }

        @Nonnull public InventoryBuilder set(int slot, @Nonnull Material material) {
            inventory.setItem(slot, new ItemStack(material)); return this;
        }

        @Nonnull public InventoryBuilder set(int slot, @Nonnull ItemStack item) {
            inventory.setItem(slot, item); return this;
        }

        @Nonnull public Inventory build() { return inventory; }
    }
}
