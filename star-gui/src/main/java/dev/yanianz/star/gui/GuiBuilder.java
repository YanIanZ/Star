package dev.yanianz.star.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Fluent builder for constructing {@link Gui} instances.
 */
public final class GuiBuilder {

    private final Component title;
    private final int rows;
    private final Map<Integer, GuiItem> slotItems = new HashMap<>();
    private Consumer<InventoryCloseEvent> closeHandler;
    private boolean draggable = false;
    private ItemStack fillItem;

    private GuiBuilder(int rows, @Nonnull Component title) {
        if (rows < 1 || rows > 6) {
            throw new IllegalArgumentException("Rows must be between 1 and 6");
        }
        this.title = title;
        this.rows = rows;
    }

    @Nonnull
    public static GuiBuilder create(int rows, @Nonnull Component title) {
        return new GuiBuilder(rows, title);
    }

    @Nonnull
    public GuiBuilder slot(int index, @Nonnull ItemStack item, @Nonnull Consumer<GuiClickEvent> handler) {
        slotItems.put(index, new GuiItem(item, handler));
        return this;
    }

    @Nonnull
    public GuiBuilder slots(int[] indices, @Nonnull ItemStack item, @Nonnull Consumer<GuiClickEvent> handler) {
        for (int index : indices) {
            slotItems.put(index, new GuiItem(item.clone(), handler));
        }
        return this;
    }

    @Nonnull
    public GuiBuilder fill(@Nonnull ItemStack item) {
        this.fillItem = item;
        return this;
    }

    @Nonnull
    public GuiBuilder fillBorder(@Nonnull ItemStack item) {
        int size = rows * 9;
        for (int i = 0; i < size; i++) {
            int row = i / 9;
            int col = i % 9;
            if (row == 0 || row == rows - 1 || col == 0 || col == 8) {
                if (!slotItems.containsKey(i)) {
                    slotItems.put(i, new GuiItem(item.clone(), e -> {}));
                }
            }
        }
        return this;
    }

    @Nonnull
    public GuiBuilder fillRect(int rowStart, int colStart, int rowEnd, int colEnd, @Nonnull ItemStack item) {
        for (int row = rowStart; row <= rowEnd; row++) {
            for (int col = colStart; col <= colEnd; col++) {
                int index = row * 9 + col;
                if (index >= 0 && index < rows * 9 && !slotItems.containsKey(index)) {
                    slotItems.put(index, new GuiItem(item.clone(), e -> {}));
                }
            }
        }
        return this;
    }

    @Nonnull
    public GuiBuilder closeHandler(@Nullable Consumer<InventoryCloseEvent> handler) {
        this.closeHandler = handler;
        return this;
    }

    @Nonnull
    public GuiBuilder draggable(boolean draggable) {
        this.draggable = draggable;
        return this;
    }

    @Nonnull
    public static PaginatedBuilder paginated(int rows, @Nonnull Component title) {
        return new PaginatedBuilder(rows, title);
    }

    @Nonnull
    public Gui build() {
        return new Gui(title, rows, slotItems, closeHandler, draggable, fillItem);
    }

    /**
     * Builder for {@link PaginatedGui}.
     */
    public static final class PaginatedBuilder {
        private final Component title;
        private final int rows;
        private final Map<Integer, GuiItem> staticItems = new HashMap<>();
        private int[] contentSlots = new int[0];
        private int nextPageSlot = -1;
        private int prevPageSlot = -1;
        private int pageIndicatorSlot = -1;
        private ItemStack nextPageItem;
        private ItemStack prevPageItem;
        private ItemStack emptyPageItem;
        private Consumer<InventoryCloseEvent> closeHandler;
        private boolean draggable = false;

        private PaginatedBuilder(int rows, @Nonnull Component title) {
            this.rows = rows;
            this.title = title;
        }

        @Nonnull
        public PaginatedBuilder contentSlots(int... slots) {
            this.contentSlots = slots;
            return this;
        }

        @Nonnull
        public PaginatedBuilder nextPageSlot(int slot, @Nonnull ItemStack item) {
            this.nextPageSlot = slot;
            this.nextPageItem = item;
            return this;
        }

        @Nonnull
        public PaginatedBuilder prevPageSlot(int slot, @Nonnull ItemStack item) {
            this.prevPageSlot = slot;
            this.prevPageItem = item;
            return this;
        }

        @Nonnull
        public PaginatedBuilder pageIndicatorSlot(int slot) {
            this.pageIndicatorSlot = slot;
            return this;
        }

        @Nonnull
        public PaginatedBuilder staticItem(int slot, @Nonnull ItemStack item, @Nonnull Consumer<GuiClickEvent> handler) {
            staticItems.put(slot, new GuiItem(item, handler));
            return this;
        }

        @Nonnull
        public PaginatedBuilder emptyPageItem(@Nonnull ItemStack item) {
            this.emptyPageItem = item;
            return this;
        }

        @Nonnull
        public PaginatedBuilder closeHandler(@Nullable Consumer<InventoryCloseEvent> handler) {
            this.closeHandler = handler;
            return this;
        }

        @Nonnull
        public PaginatedBuilder draggable(boolean draggable) {
            this.draggable = draggable;
            return this;
        }

        @Nonnull
        public PaginatedGui build() {
            return new PaginatedGui(title, rows, staticItems, contentSlots,
                nextPageSlot, prevPageSlot, pageIndicatorSlot,
                nextPageItem, prevPageItem, emptyPageItem, closeHandler, draggable);
        }
    }
}
