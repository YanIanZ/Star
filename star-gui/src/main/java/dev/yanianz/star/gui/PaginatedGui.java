package dev.yanianz.star.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A multi-page {@link Gui} that distributes items across pages with navigation.
 */
public class PaginatedGui extends Gui {

    private final int[] contentSlots;
    private final int nextPageSlot;
    private final int prevPageSlot;
    private final int pageIndicatorSlot;
    private final ItemStack nextPageItem;
    private final ItemStack prevPageItem;
    private final ItemStack emptyPageItem;
    private final Map<Integer, GuiItem> staticItems;
    private final int rows;
    private final Component title;

    private List<GuiItem> items = new ArrayList<>();
    private int currentPage = 0;

    PaginatedGui(@Nonnull Component title, int rows, @Nonnull Map<Integer, GuiItem> staticItems,
                 @Nonnull int[] contentSlots, int nextPageSlot, int prevPageSlot, int pageIndicatorSlot,
                 @Nullable ItemStack nextPageItem, @Nullable ItemStack prevPageItem,
                 @Nullable ItemStack emptyPageItem, @Nullable Consumer<InventoryCloseEvent> closeHandler,
                 boolean draggable) {
        super(title, rows, new HashMap<>(), closeHandler, draggable, null);
        this.title = title;
        this.rows = rows;
        this.staticItems = staticItems;
        this.contentSlots = contentSlots;
        this.nextPageSlot = nextPageSlot;
        this.prevPageSlot = prevPageSlot;
        this.pageIndicatorSlot = pageIndicatorSlot;
        this.nextPageItem = nextPageItem;
        this.prevPageItem = prevPageItem;
        this.emptyPageItem = emptyPageItem;
        renderPage();
    }

    /**
     * Replace all items and reset to the first page.
     */
    public void setItems(@Nonnull List<ItemStack> itemStacks) {
        this.items = itemStacks.stream().map(i -> new GuiItem(i, e -> {})).toList();
        this.currentPage = 0;
        renderPage();
    }

    /**
     * Replace all items with handlers and reset to the first page.
     */
    public void setItemsWithHandlers(@Nonnull List<Consumer<GuiClickEvent>> handlers, @Nonnull List<ItemStack> itemStacks) {
        this.items = new ArrayList<>();
        int count = Math.min(handlers.size(), itemStacks.size());
        for (int i = 0; i < count; i++) {
            this.items.add(new GuiItem(itemStacks.get(i), handlers.get(i)));
        }
        this.currentPage = 0;
        renderPage();
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        if (contentSlots.length == 0 || items.isEmpty()) return 1;
        return (items.size() + contentSlots.length - 1) / contentSlots.length;
    }

    public void nextPage(@Nonnull Player player, @Nonnull Plugin plugin) {
        if (currentPage < getTotalPages() - 1) {
            currentPage++;
            renderPage();
            player.openInventory(getInventory());
        }
    }

    public void prevPage(@Nonnull Player player, @Nonnull Plugin plugin) {
        if (currentPage > 0) {
            currentPage--;
            renderPage();
            player.openInventory(getInventory());
        }
    }

    private void renderPage() {
        getInventory().clear();
        slotItems.clear();

        for (Map.Entry<Integer, GuiItem> entry : staticItems.entrySet()) {
            getInventory().setItem(entry.getKey(), entry.getValue().item());
            slotItems.put(entry.getKey(), entry.getValue());
        }

        int start = currentPage * contentSlots.length;
        for (int i = 0; i < contentSlots.length; i++) {
            int itemIndex = start + i;
            if (itemIndex < items.size()) {
                GuiItem guiItem = items.get(itemIndex);
                getInventory().setItem(contentSlots[i], guiItem.item());
                slotItems.put(contentSlots[i], guiItem);
            } else if (emptyPageItem != null) {
                getInventory().setItem(contentSlots[i], emptyPageItem.clone());
            }
        }

        if (currentPage > 0 && prevPageSlot >= 0 && prevPageItem != null) {
            getInventory().setItem(prevPageSlot, prevPageItem.clone());
        }
        if (currentPage < getTotalPages() - 1 && nextPageSlot >= 0 && nextPageItem != null) {
            getInventory().setItem(nextPageSlot, nextPageItem.clone());
        }
    }
}
