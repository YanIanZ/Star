package dev.yanianz.star.gui;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * Represents an ItemStack paired with a click handler in a Gui.
 */
public final class GuiItem {

    private final ItemStack item;
    private final Consumer<GuiClickEvent> handler;

    public GuiItem(@Nonnull ItemStack item, @Nonnull Consumer<GuiClickEvent> handler) {
        this.item = item;
        this.handler = handler;
    }

    @Nonnull
    public ItemStack getItem() {
        return item;
    }

    @Nonnull
    public Consumer<GuiClickEvent> getHandler() {
        return handler;
    }
}
