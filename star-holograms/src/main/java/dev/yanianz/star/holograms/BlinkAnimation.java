package dev.yanianz.star.holograms;

import net.kyori.adventure.text.Component;
import javax.annotation.Nonnull;

public final class BlinkAnimation implements HologramAnimation {
    private final int interval;

    public BlinkAnimation(int interval) { this.interval = interval; }

    @Override @Nonnull
    public Component apply(@Nonnull HologramLine line, int tick) {
        if ((tick / interval) % 2 == 0) return line.getText() != null ? line.getText() : Component.empty();
        return Component.empty();
    }
}
