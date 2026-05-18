package dev.yanianz.star.common;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import javax.annotation.Nonnull;

import org.bukkit.ChatColor;

/**
 * Utilities related to {@link ChatColor} and Adventure {@link Component} formatting.
 *
 * @author TheBusyBiscuit
 */
public final class ChatColors {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private ChatColors() {}

    /**
     * Shortcut for: <code>ChatColor.translateAlternateColorCodes('&amp;', input)</code>
     *
     * @param input The String to colorize
     * @return The colorized String
     */
    public static @Nonnull String color(@Nonnull String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    /**
     * Colors the given String in alternating Bukkit ChatColors.
     *
     * @param text   The String to color
     * @param colors The Colors to apply
     * @return The alternating-colored String
     */
    public static @Nonnull String alternating(@Nonnull String text, ChatColor... colors) {
        int i = 0;
        StringBuilder builder = new StringBuilder(text.length() * 3);

        for (char c : text.toCharArray()) {
            builder.append(colors[i % colors.length].toString()).append(c);
            i++;
        }

        return builder.toString();
    }

    /**
     * Parses legacy {@code &} color codes into an Adventure {@link Component}.
     *
     * @param input The legacy color-coded String
     * @return The parsed Component
     */
    public static @Nonnull Component legacyToComponent(@Nonnull String input) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(input);
    }

    /**
     * Translates {@code &} color codes and returns an Adventure {@link Component}.
     *
     * @param input The legacy color-coded String
     * @return The colorized Component
     */
    public static @Nonnull Component colorToComponent(@Nonnull String input) {
        return legacyToComponent(input);
    }

    /**
     * Creates a {@link TextColor} from a hex string. Accepts both {@code "#FF5500"} and {@code "FF5500"}.
     *
     * @param hex The hex color string
     * @return The TextColor
     * @throws IllegalArgumentException if the hex string is invalid
     */
    public static @Nonnull TextColor hex(@Nonnull String hex) {
        String cleaned = hex.startsWith("#") ? hex.substring(1) : hex;
        if (cleaned.length() != 6) {
            throw new IllegalArgumentException("Hex color must be 6 characters, got: " + hex);
        }
        TextColor color = TextColor.fromHexString("#" + cleaned);
        if (color == null) {
            throw new IllegalArgumentException("Invalid hex color: " + hex);
        }
        return color;
    }

    /**
     * Creates a colored {@link Component} with the given hex color applied.
     *
     * @param hex  The hex color string
     * @param text The text to color
     * @return The colored Component
     */
    public static @Nonnull Component hexToComponent(@Nonnull String hex, @Nonnull String text) {
        return Component.text(text).color(hex(hex));
    }

    /**
     * Creates a colored {@link Component} with the given hex color applied (no text content).
     *
     * @param hex The hex color string
     * @return An empty Component with the color applied
     */
    public static @Nonnull Component hexToComponent(@Nonnull String hex) {
        return Component.text("").color(hex(hex));
    }

    /**
     * Parses a MiniMessage formatted string into an Adventure {@link Component}.
     *
     * @param input The MiniMessage string
     * @return The parsed Component
     */
    public static @Nonnull Component miniMessage(@Nonnull String input) {
        return MINI_MESSAGE.deserialize(input);
    }

    /**
     * Colors the given String in alternating Adventure TextColors.
     *
     * @param text   The text to color
     * @param colors The TextColors to alternate
     * @return The alternating-colored Component
     */
    public static @Nonnull Component alternating(@Nonnull String text, TextColor... colors) {
        Component component = Component.empty();
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            component = component.append(Component.text(String.valueOf(chars[i])).color(colors[i % colors.length]));
        }
        return component;
    }

    /**
     * Creates a gradient color effect across the text, transitioning from the start color to the end color.
     *
     * @param text  The text to apply the gradient to
     * @param start The start color of the gradient
     * @param end   The end color of the gradient
     * @return The gradient-colored Component
     */
    public static @Nonnull Component gradient(@Nonnull String text, @Nonnull TextColor start, @Nonnull TextColor end) {
        Component component = Component.empty();
        char[] chars = text.toCharArray();
        int length = Math.max(1, chars.length - 1);
        for (int i = 0; i < chars.length; i++) {
            float ratio = (float) i / length;
            int r = (int) (start.red() + (end.red() - start.red()) * ratio);
            int g = (int) (start.green() + (end.green() - start.green()) * ratio);
            int b = (int) (start.blue() + (end.blue() - start.blue()) * ratio);
            component = component.append(Component.text(String.valueOf(chars[i])).color(TextColor.color(r, g, b)));
        }
        return component;
    }
}
