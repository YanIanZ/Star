package dev.yanianz.star.gui;

import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.*;

import java.util.concurrent.atomic.AtomicInteger;

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
}
