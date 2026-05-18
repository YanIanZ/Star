package dev.yanianz.star.test;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import javax.annotation.Nonnull;
import static org.junit.jupiter.api.Assertions.*;

public final class TestAssertions {
    private TestAssertions() {}

    public static void assertItemEquals(@Nonnull ItemStack expected, @Nonnull ItemStack actual) {
        assertEquals(expected.getType(), actual.getType(), "Item types differ");
        assertEquals(expected.getAmount(), actual.getAmount(), "Item amounts differ");
    }

    public static void assertLocationEquals(@Nonnull Location expected, @Nonnull Location actual) {
        assertEquals(expected.getWorld(), actual.getWorld(), "Worlds differ");
        assertEquals(expected.getBlockX(), actual.getBlockX(), "Block X differs");
        assertEquals(expected.getBlockY(), actual.getBlockY(), "Block Y differs");
        assertEquals(expected.getBlockZ(), actual.getBlockZ(), "Block Z differs");
    }

    public static void assertContains(@Nonnull String haystack, @Nonnull String needle) {
        assertTrue(haystack.contains(needle), "Expected '" + haystack + "' to contain '" + needle + "'");
    }
}
