package dev.yanianz.star.test;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.mockbukkit.mockbukkit.MockBukkit;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

@DisplayName("StarTest")
class TestStarTest {
    @BeforeAll static void setUp() { MockBukkit.mock(); }
    @AfterAll static void tearDown() { MockBukkit.unmock(); }

    @Test @DisplayName("PlayerFactory builds mock")
    void playerFactory() {
        PlayerFactory.PlayerMock p = PlayerFactory.create("Steve").withHealth(15).withLevel(5).withExp(0.5f);
        assertEquals("Steve", p.getName());
        assertEquals(15.0, p.getHealth());
        assertEquals(5, p.getLevel());
        assertEquals(0.5f, p.getExp());
    }

    @Test @DisplayName("ItemFactory builds item")
    void itemFactory() {
        ItemStack item = ItemFactory.create(Material.DIAMOND_SWORD).name("Blade").amount(2).build();
        assertEquals(Material.DIAMOND_SWORD, item.getType());
        assertEquals(2, item.getAmount());
    }

    @Test @DisplayName("SchedulerMock ticks")
    void schedulerMock() {
        SchedulerMock scheduler = new SchedulerMock();
        boolean[] ran = {false};
        scheduler.runTaskLater(() -> ran[0] = true, 1);
        scheduler.tick();
        assertTrue(ran[0]);
    }

    @Test @DisplayName("TestAssertions works")
    void testAssertions() {
        ItemStack a = new ItemStack(Material.STONE, 3);
        ItemStack b = new ItemStack(Material.STONE, 3);
        TestAssertions.assertItemEquals(a, b);
    }

    @Test @DisplayName("SchedulerMock tick counter")
    void schedulerTicks() {
        SchedulerMock s = new SchedulerMock();
        s.tickTimes(5);
        assertEquals(5, s.getTick());
    }
}
