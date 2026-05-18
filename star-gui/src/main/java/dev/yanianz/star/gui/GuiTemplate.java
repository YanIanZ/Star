package dev.yanianz.star.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A reusable GUI layout that can be instantiated with different data per player.
 *
 * @param <T> The data type passed to slot providers
 */
public final class GuiTemplate<T> {

    private final Component title;
    private final int rows;
    private final Map<Integer, SlotProvider<T>> slotProviders = new HashMap<>();
    private Consumer<InventoryCloseEvent> closeHandler;
    private boolean draggable = false;

    private GuiTemplate(int rows, @Nonnull Component title) {
        this.title = title;
        this.rows = rows;
    }

    @Nonnull
    public static <T> GuiTemplate<T> create(int rows, @Nonnull Component title) {
        return new GuiTemplate<>(rows, title);
    }

    @Nonnull
    public GuiTemplate<T> slot(int index, @Nonnull Function<T, ItemStack> itemProvider,
                                @Nonnull BiConsumer<T, GuiClickEvent> handlerProvider) {
        slotProviders.put(index, new SlotProvider<>(itemProvider, handlerProvider));
        return this;
    }

    @Nonnull
    public GuiTemplate<T> closeHandler(@Nonnull Consumer<InventoryCloseEvent> handler) {
        this.closeHandler = handler;
        return this;
    }

    @Nonnull
    public GuiTemplate<T> draggable(boolean draggable) {
        this.draggable = draggable;
        return this;
    }

    @Nonnull
    public Gui build(@Nonnull Player player, @Nonnull T data, @Nonnull Plugin plugin) {
        GuiBuilder builder = GuiBuilder.create(rows, title)
            .closeHandler(closeHandler)
            .draggable(draggable);

        for (Map.Entry<Integer, SlotProvider<T>> entry : slotProviders.entrySet()) {
            SlotProvider<T> provider = entry.getValue();
            ItemStack item = provider.itemProvider.apply(data);
            if (item != null) {
                builder.slot(entry.getKey(), item, e -> provider.handlerProvider.accept(data, e));
            }
        }

        return builder.build();
    }

    private record SlotProvider<T>(
        @Nonnull Function<T, ItemStack> itemProvider,
        @Nonnull BiConsumer<T, GuiClickEvent> handlerProvider
    ) {}
}
