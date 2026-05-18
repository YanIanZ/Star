package dev.yanianz.star.holograms;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import javax.annotation.Nonnull;

public final class ScrollAnimation implements HologramAnimation {
    private final int width;
    private int offset;

    public ScrollAnimation(int width) { this.width = width; }

    @Override @Nonnull
    public Component apply(@Nonnull HologramLine line, int tick) {
        offset = (tick / 2) % Math.max(1, getText(line).length() + width);
        String text = getText(line);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < width; i++) {
            int idx = (i + offset) % Math.max(1, text.length());
            sb.append(text.charAt(idx));
        }
        return Component.text(sb.toString());
    }

    private String getText(HologramLine line) {
        if (line.getText() instanceof TextComponent tc) return tc.content();
        return line.getText() != null ? line.getText().toString() : "";
    }
}
