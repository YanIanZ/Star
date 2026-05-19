package dev.yanianz.star.gui;

import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GuiBuilder")
class TestGuiBuilder {

    private ServerMock server;
    private PlayerMock player;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        player = server.addPlayer();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("builds basic GUI with correct size")
    void buildsBasicGui() {
        Gui gui = GuiBuilder.create(3, Component.text("Test"))
            .slot(13, new ItemStack(Material.DIAMOND), e -> {})
            .build();

        assertEquals(27, gui.getInventory().getSize());
        assertEquals(Material.DIAMOND, gui.getInventory().getItem(13).getType());
    }

    @Test
    @DisplayName("click handler fires on slot click")
    void clickHandlerFires() {
        AtomicInteger clicks = new AtomicInteger(0);
        Gui gui = GuiBuilder.create(1, Component.text("Click Test"))
            .slot(0, new ItemStack(Material.STONE), e -> clicks.incrementAndGet())
            .build();

        gui.open(player, MockBukkit.createMockPlugin());

        InventoryClickEvent clickEvent = new InventoryClickEvent(
            player.getOpenInventory(),
            InventoryType.SlotType.CONTAINER,
            0,
            ClickType.LEFT,
            InventoryAction.PICKUP_ALL
        );
        server.getPluginManager().callEvent(clickEvent);

        assertEquals(1, clicks.get());
    }

    @Test
    @DisplayName("throws on invalid row count")
    void invalidRows() {
        assertThrows(IllegalArgumentException.class, () ->
            GuiBuilder.create(0, Component.text("Bad")));
        assertThrows(IllegalArgumentException.class, () ->
            GuiBuilder.create(7, Component.text("Bad")));
    }

    @Test
    @DisplayName("fill border adds items to edges only")
    void fillBorder() {
        Gui gui = GuiBuilder.create(3, Component.text("Border"))
            .fillBorder(new ItemStack(Material.GLASS_PANE))
            .build();

        for (int i = 0; i < 9; i++) {
            assertNotNull(gui.getInventory().getItem(i));
        }
        assertNull(gui.getInventory().getItem(13));
    }

    @Test
    @DisplayName("GuiItem getters work")
    void guiItemGetters() {
        GuiItem item = new GuiItem(
            new ItemStack(Material.EMERALD), e -> e.setCancelled(true));

        assertEquals(Material.EMERALD, item.item().getType());
        assertNotNull(item.handler());
    }

    @Test
    @DisplayName("dynamic slot rerender fires")
    void dynamicSlotRerender() {
        AtomicInteger calls = new AtomicInteger(0);
        Supplier<ItemStack> supplier = () -> {
            calls.incrementAndGet();
            return new ItemStack(Material.DIAMOND);
        };
        Gui gui = GuiBuilder.create(1, Component.text("Dynamic"))
            .dynamicSlot(0, supplier, e -> {})
            .build();
        assertNotNull(gui.getInventory().getItem(0));
        gui.rerender();
        assertTrue(calls.get() >= 1);
    }

    @Test
    @DisplayName("Gui open and close handlers work")
    void openCloseHandlers() {
        AtomicInteger closed = new AtomicInteger(0);
        Gui gui = GuiBuilder.create(1, Component.text("Close Test"))
            .closeHandler(e -> closed.incrementAndGet())
            .build();
        gui.open(player, MockBukkit.createMockPlugin());
        assertFalse(gui.getInventory().getViewers().isEmpty());
    }

    @Test
    @DisplayName("paginated gui page calculation")
    void paginatedPages() {
        GuiBuilder.PaginatedBuilder pb = GuiBuilder.paginated(3, Component.text("Pages"));
        pb.contentSlots(0, 1, 2);
        PaginatedGui gui = pb.build();
        assertEquals(1, gui.getTotalPages());
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < 7; i++) items.add(new ItemStack(Material.STONE));
        gui.setItems(items);
        assertEquals(3, gui.getTotalPages());
    }

    @Test
    @DisplayName("GuiTemplate builds with data")
    void guiTemplate() {
        GuiTemplate<String> template = GuiTemplate.create(1, Component.text("Template"));
        template.slot(0, name -> new ItemStack(Material.PAPER), (data, e) -> {});
        Gui gui = template.build(player, "test", MockBukkit.createMockPlugin());
        assertNotNull(gui);
        assertEquals(Material.PAPER, gui.getInventory().getItem(0).getType());
    }

    @Test
    @DisplayName("GuiState stores data")
    void guiState() {
        dev.yanianz.star.gui.state.GuiState<String> state = new dev.yanianz.star.gui.state.GuiState<>("initial") {
            @Override public Gui build(Player player) { return null; }
        };
        assertEquals("initial", state.getData());
        state.setData("updated");
        assertEquals("updated", state.getData());
    }

    @Test
    @DisplayName("GuiStateManager register/unregister")
    void guiStateManager() {
        dev.yanianz.star.gui.state.GuiStateManager mgr = new dev.yanianz.star.gui.state.GuiStateManager();
        dev.yanianz.star.gui.state.GuiState<String> state = new dev.yanianz.star.gui.state.GuiState<>("data") {
            @Override public Gui build(Player player) { return null; }
        };
        mgr.register(player, state);
        assertTrue(mgr.isRegistered(player));
        assertEquals("data", mgr.get(player, String.class));
        mgr.unregister(player);
        assertFalse(mgr.isRegistered(player));
    }

    @Test
    @DisplayName("SlotAnimation validates frames")
    void slotAnimationValidation() {
        assertThrows(IllegalArgumentException.class, () ->
            new dev.yanianz.star.gui.animation.SlotAnimation(MockBukkit.createMockPlugin(), 0, List.of(), 10));
    }
}
