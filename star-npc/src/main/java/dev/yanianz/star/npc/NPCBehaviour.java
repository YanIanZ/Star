package dev.yanianz.star.npc;

import org.bukkit.entity.Player;
import javax.annotation.Nonnull;

/** Interface for NPC behaviours that run on tick. */
public interface NPCBehaviour {
    void tick(@Nonnull NPC npc);
    default void onInteract(@Nonnull NPC npc, @Nonnull Player player) {}
}
