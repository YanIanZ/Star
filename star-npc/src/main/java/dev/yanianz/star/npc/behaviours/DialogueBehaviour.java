package dev.yanianz.star.npc.behaviours;

import dev.yanianz.star.npc.*;
import org.bukkit.entity.Player;
import javax.annotation.Nonnull;

/** Triggers dialogue on interact. */
public final class DialogueBehaviour implements NPCBehaviour {
    private final NPCDialogue dialogue;

    public DialogueBehaviour(@Nonnull NPCDialogue dialogue) { this.dialogue = dialogue; }

    @Override
    public void tick(@Nonnull NPC npc) {}

    @Override
    public void onInteract(@Nonnull NPC npc, @Nonnull Player player) {
        player.sendMessage(dialogue.text());
    }
}
