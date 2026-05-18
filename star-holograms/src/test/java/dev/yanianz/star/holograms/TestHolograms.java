package dev.yanianz.star.holograms;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("Holograms")
class TestHolograms {
    @Test @DisplayName("HologramLine text/item switch")
    void lineSwitch() {
        HologramLine line = new HologramLine(Component.text("Test"));
        assertTrue(line.isText());
        assertFalse(line.isItem());
        assertEquals("Test", net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(line.getText()));
    }

    @Test @DisplayName("Hologram add/remove lines")
    void hologramLines() {
        Location loc = mock(Location.class);
        Hologram holo = new Hologram("test", loc);
        holo.addLine(new HologramLine(Component.text("A")));
        holo.addLine(new HologramLine(Component.text("B")));
        assertEquals(2, holo.getLineCount());
        holo.insertLine(1, new HologramLine(Component.text("C")));
        assertEquals(3, holo.getLineCount());
        holo.removeLine(0);
        assertEquals(2, holo.getLineCount());
        holo.clearLines();
        assertEquals(0, holo.getLineCount());
    }

    @Test @DisplayName("ScrollAnimation offsets text")
    void scrollAnimation() {
        HologramLine line = new HologramLine(Component.text("Hello"));
        ScrollAnimation anim = new ScrollAnimation(3);
        Component result = anim.apply(line, 0);
        assertNotNull(result);
    }

    @Test @DisplayName("RainbowAnimation applies colors")
    void rainbowAnimation() {
        HologramLine line = new HologramLine(Component.text("Hi"));
        RainbowAnimation anim = new RainbowAnimation();
        Component result = anim.apply(line, 0);
        assertNotNull(result);
    }

    @Test @DisplayName("BlinkAnimation toggles")
    void blinkAnimation() {
        HologramLine line = new HologramLine(Component.text("Blink"));
        BlinkAnimation anim = new BlinkAnimation(1);
        Component on = anim.apply(line, 0);
        Component off = anim.apply(line, 1);
        assertNotNull(on);
        assertEquals("", net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(off));
    }
}
