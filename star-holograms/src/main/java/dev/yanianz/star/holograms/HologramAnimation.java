package dev.yanianz.star.holograms;

import net.kyori.adventure.text.Component;
import javax.annotation.Nonnull;

public interface HologramAnimation {
    @Nonnull Component apply(@Nonnull HologramLine line, int tick);
}
