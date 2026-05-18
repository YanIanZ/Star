package dev.yanianz.star.common;

import static org.junit.jupiter.api.Assertions.*;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ChatColors")
class TestChatColors {

    @Nested
    @DisplayName("hex()")
    class Hex {

        @Test
        @DisplayName("parse hex with # prefix")
        void withHashPrefix() {
            TextColor color = ChatColors.hex("#FF5500");
            assertEquals(0xFF, color.red());
            assertEquals(0x55, color.green());
            assertEquals(0x00, color.blue());
        }

        @Test
        @DisplayName("parse hex without # prefix")
        void withoutHashPrefix() {
            TextColor color = ChatColors.hex("FF5500");
            assertEquals(0xFF, color.red());
            assertEquals(0x55, color.green());
            assertEquals(0x00, color.blue());
        }

        @Test
        @DisplayName("parse white")
        void white() {
            TextColor color = ChatColors.hex("#FFFFFF");
            assertEquals(0xFF, color.red());
            assertEquals(0xFF, color.green());
            assertEquals(0xFF, color.blue());
        }

        @Test
        @DisplayName("parse black")
        void black() {
            TextColor color = ChatColors.hex("#000000");
            assertEquals(0x00, color.red());
            assertEquals(0x00, color.green());
            assertEquals(0x00, color.blue());
        }

        @Test
        @DisplayName("throws on invalid hex length")
        void invalidLength() {
            assertThrows(IllegalArgumentException.class, () -> ChatColors.hex("#FFF"));
            assertThrows(IllegalArgumentException.class, () -> ChatColors.hex("12345"));
            assertThrows(IllegalArgumentException.class, () -> ChatColors.hex("#1234567"));
        }

        @Test
        @DisplayName("throws on non-hex characters")
        void nonHexChars() {
            assertThrows(IllegalArgumentException.class, () -> ChatColors.hex("#GGGGGG"));
            assertThrows(IllegalArgumentException.class, () -> ChatColors.hex("ZZZZZZ"));
        }
    }

    @Nested
    @DisplayName("legacyToComponent()")
    class LegacyToComponent {

        @Test
        @DisplayName("parses ampersand codes")
        void parsesAmpersandCodes() {
            Component result = ChatColors.legacyToComponent("&cHello &aWorld");
            String expectedPlain = "Hello World";
            assertEquals(expectedPlain, plain(result));
        }

        @Test
        @DisplayName("returns plain text for no codes")
        void plainText() {
            Component result = ChatColors.legacyToComponent("Hello World");
            assertEquals("Hello World", plain(result));
        }
    }

    @Nested
    @DisplayName("miniMessage()")
    class MiniMessage {

        @Test
        @DisplayName("parses MiniMessage color tag")
        void parsesColorTag() {
            Component result = ChatColors.miniMessage("<color:#FF5500>Hello</color>");
            assertEquals("Hello", plain(result));
        }

        @Test
        @DisplayName("returns plain text for no tags")
        void plainText() {
            Component result = ChatColors.miniMessage("Hello World");
            assertEquals("Hello World", plain(result));
        }
    }

    @Nested
    @DisplayName("alternating() with TextColor")
    class AlternatingTextColor {

        @Test
        @DisplayName("alternates between two colors")
        void twoColors() {
            Component result = ChatColors.alternating("AB", NamedTextColor.RED, NamedTextColor.BLUE);
            String plain = plain(result);
            assertEquals("AB", plain);
        }
    }

    @Nested
    @DisplayName("gradient()")
    class Gradient {

        @Test
        @DisplayName("creates gradient for multi-char text")
        void multiChar() {
            Component result = ChatColors.gradient("AB", NamedTextColor.RED, NamedTextColor.BLUE);
            assertEquals("AB", plain(result));
        }

        @Test
        @DisplayName("handles single character")
        void singleChar() {
            Component result = ChatColors.gradient("A", NamedTextColor.RED, NamedTextColor.BLUE);
            assertEquals("A", plain(result));
        }
    }

    private static String plain(Component component) {
        return net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(component);
    }
}
