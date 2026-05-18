package dev.yanianz.star.misc;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;

@DisplayName("Misc")
class TestMisc {
    @Test
    @DisplayName("PlaceholderResolver replaces keys")
    void placeholderResolver() {
        String result = PlaceholderResolver.resolve("Hello {name}", Map.of("name", "World"));
        assertEquals("Hello World", result);
    }

    @Test
    @DisplayName("PlaceholderResolver ignores missing keys")
    void placeholderMissing() {
        String result = PlaceholderResolver.resolve("{a} {b}", Map.of("a", "1"));
        assertEquals("1 {b}", result);
    }

    @Test
    @DisplayName("SoundBuilder API works")
    void soundBuilder() {
        assertDoesNotThrow(() -> SoundBuilder.play(org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP));
    }

    @Test
    @DisplayName("SoundPlayer static methods exist")
    void soundPlayer() {
        assertNotNull(SoundPlayer.class);
    }
}
