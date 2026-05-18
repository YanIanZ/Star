package dev.yanianz.star.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Wrapper around {@link InventoryClickEvent} for Gui click handling.
 */
public final class GuiClickEvent {

    private final InventoryClickEvent event;

    public GuiClickEvent(@Nonnull InventoryClickEvent event) {
        this.event = event;
    }

    @Nonnull
    public Player player() {
        return (Player) event.getWhoClicked();
    }

    public int slot() {
        return event.getSlot();
    }

    @Nonnull
    public ClickType clickType() {
        return event.getClick();
    }

    public boolean isLeftClick() {
        return event.isLeftClick();
    }

    public boolean isRightClick() {
        return event.isRightClick();
    }

    public boolean isShiftClick() {
        return event.isShiftClick();
    }

    @Nullable
    public ItemStack currentItem() {
        return event.getCurrentItem();
    }

    @Nullable
    public ItemStack cursorItem() {
        return event.getCursor();
    }

    public void setCancelled(boolean cancelled) {
        event.setCancelled(cancelled);
    }

    @Nonnull
    public InventoryClickEvent getEvent() {
        return event;
    }
}
