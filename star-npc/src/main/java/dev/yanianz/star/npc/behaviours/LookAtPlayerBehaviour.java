package dev.yanianz.star.npc.behaviours;

import dev.yanianz.star.npc.*;
import io.papermc.paper.entity.LookAnchor;
import org.bukkit.entity.Player;
import javax.annotation.Nonnull;

/** Makes NPC look at the nearest player within range. */
public final class LookAtPlayerBehaviour implements NPCBehaviour {
    private final double range;

    public LookAtPlayerBehaviour(double range) { this.range = range; }

    @Override
    public void tick(@Nonnull NPC npc) {
        if (npc.getEntity() == null) return;
        Player nearest = npc.getEntity().getWorld().getNearbyPlayers(npc.getEntity().getLocation(), range, p -> true)
            .stream().findFirst().orElse(null);
        if (nearest != null) npc.getEntity().lookAt(nearest.getEyeLocation(), LookAnchor.EYES);
    }
}
