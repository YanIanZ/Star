package dev.yanianz.star.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A GUI wrapper around a Bukkit {@link Inventory} with click handling.
 */
public class Gui implements Listener {

    private final Inventory inventory;
    private final Map<Integer, GuiItem> slotItems;
    private final Consumer<InventoryCloseEvent> closeHandler;
    private final boolean draggable;
    private final ItemStack fillItem;
    private boolean registered = false;

    Gui(@Nonnull Component title, int rows, @Nonnull Map<Integer, GuiItem> slotItems,
        @Nullable Consumer<InventoryCloseEvent> closeHandler, boolean draggable,
        @Nullable ItemStack fillItem) {
        this.inventory = Bukkit.createInventory(null, rows * 9, title);
        this.slotItems = new HashMap<>(slotItems);
        this.closeHandler = closeHandler;
        this.draggable = draggable;
        this.fillItem = fillItem;

        for (Map.Entry<Integer, GuiItem> entry : slotItems.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().getItem());
        }
        if (fillItem != null) {
            for (int i = 0; i < inventory.getSize(); i++) {
                if (inventory.getItem(i) == null) {
                    inventory.setItem(i, fillItem.clone());
                }
            }
        }
    }

    @Nonnull
    public Inventory getInventory() {
        return inventory;
    }

    public void open(@Nonnull Player player, @Nonnull Plugin plugin) {
        if (!registered) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
            registered = true;
        }
        player.openInventory(inventory);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(inventory)) {
            return;
        }
        if (!draggable) {
            event.setCancelled(true);
        }
        GuiItem guiItem = slotItems.get(event.getSlot());
        if (guiItem != null) {
            guiItem.getHandler().accept(new GuiClickEvent(event));
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (!event.getInventory().equals(inventory)) {
            return;
        }
        if (!draggable) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!event.getInventory().equals(inventory)) {
            return;
        }
        if (closeHandler != null) {
            closeHandler.accept(event);
        }
        HandlerList.unregisterAll(this);
        registered = false;
    }

    /**
     * Updates the item in a slot without rebuilding the GUI.
     */
    public void setItem(int slot, @Nonnull ItemStack item, @Nullable Consumer<GuiClickEvent> handler) {
        inventory.setItem(slot, item);
        if (handler != null) {
            slotItems.put(slot, new GuiItem(item, handler));
        } else {
            slotItems.remove(slot);
        }
    }

    /**
     * Refreshes all slot items from the current slotItems map.
     */
    public void refresh() {
        for (Map.Entry<Integer, GuiItem> entry : slotItems.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().getItem());
        }
    }
}
