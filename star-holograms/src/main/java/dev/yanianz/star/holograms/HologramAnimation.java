package dev.yanianz.star.holograms;

import net.kyori.adventure.text.Component;
import javax.annotation.Nonnull;

public sealed interface HologramAnimation permits ScrollAnimation, RainbowAnimation, BlinkAnimation {
    @Nonnull Component apply(@Nonnull HologramLine line, int tick);
}
