package dev.yanianz.star.npc.behaviours;

import dev.yanianz.star.npc.*;
import org.bukkit.Location;
import javax.annotation.Nonnull;

/** Random pathfinding within a radius. */
public final class WanderBehaviour implements NPCBehaviour {
    private final double radius;
    private Location target;

    public WanderBehaviour(double radius) { this.radius = radius; }

    @Override
    public void tick(@Nonnull NPC npc) {
        if (npc.getEntity() == null || npc.getSpawnLocation() == null) return;
        if (target == null || npc.getEntity().getLocation().distanceSquared(target) < 2) {
            double angle = Math.random() * 2 * Math.PI;
            double dist = Math.random() * radius;
            Location base = npc.getSpawnLocation();
            target = base.clone().add(Math.cos(angle) * dist, 0, Math.sin(angle) * dist);
        }
        if (npc.getEntity() instanceof org.bukkit.entity.Mob mob) mob.getPathfinder().moveTo(target);
    }
}
