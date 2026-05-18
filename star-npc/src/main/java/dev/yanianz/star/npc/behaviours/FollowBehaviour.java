package dev.yanianz.star.npc.behaviours;

import dev.yanianz.star.npc.*;
import org.bukkit.entity.Player;
import javax.annotation.Nonnull;

/** Follows a target player. */
public final class FollowBehaviour implements NPCBehaviour {
    private final Player target;
    private final double minDistance;

    public FollowBehaviour(@Nonnull Player target, double minDistance) {
        this.target = target;
        this.minDistance = minDistance;
    }

    @Override
    public void tick(@Nonnull NPC npc) {
        if (npc.getEntity() == null || !target.isOnline()) return;
        if (npc.getEntity().getLocation().distanceSquared(target.getLocation()) > minDistance * minDistance)
            if (npc.getEntity() instanceof org.bukkit.entity.Mob mob) mob.getPathfinder().moveTo(target.getLocation());
    }
}
