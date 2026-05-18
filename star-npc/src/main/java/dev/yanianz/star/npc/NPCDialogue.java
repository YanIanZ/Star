package dev.yanianz.star.npc;

import net.kyori.adventure.text.Component;
import javax.annotation.Nonnull;

/** Dialogue node with text and optional responses. */
public final class NPCDialogue {
    private final Component text;

    public NPCDialogue(@Nonnull Component text) { this.text = text; }

    @Nonnull
    public Component getText() { return text; }
}
