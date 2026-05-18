package dev.yanianz.star.holograms;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import javax.annotation.Nonnull;
import java.awt.Color;

public final class RainbowAnimation implements HologramAnimation {
    @Override @Nonnull
    public Component apply(@Nonnull HologramLine line, int tick) {
        if (!line.isText() || line.getText() == null) return Component.empty();
        String text = PlainTextComponentSerializer.plainText().serialize(line.getText());
        Component result = Component.empty();
        for (int i = 0; i < text.length(); i++) {
            float hue = ((tick + i * 10) % 360) / 360f;
            Color c = Color.getHSBColor(hue, 1, 1);
            result = result.append(Component.text(text.charAt(i)).color(TextColor.color(c.getRed(), c.getGreen(), c.getBlue())));
        }
        return result;
    }
}
