package dev.yanianz.star.npc;

import dev.yanianz.star.npc.behaviours.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;

/** Represents a spawned NPC entity with behaviours. */
public final class NPC {
    private final String id;
    private final NPCProfile profile;
    private final Location spawnLocation;
    private LivingEntity entity;
    private final List<NPCBehaviour> behaviours = new CopyOnWriteArrayList<>();
    private BiConsumer<Player, org.bukkit.event.inventory.ClickType> interactHandler;

    public NPC(@Nonnull String id, @Nonnull NPCProfile profile, @Nullable Location spawnLocation) {
        this.id = id;
        this.profile = profile;
        this.spawnLocation = spawnLocation != null ? spawnLocation.clone() : null;
    }

    @Nonnull
    public String getId() { return id; }

    @Nonnull
    public NPCProfile getProfile() { return profile; }

    @Nullable
    public Location getSpawnLocation() { return spawnLocation; }

    @Nullable
    public LivingEntity getEntity() { return entity; }

    public void spawn() {
        if (entity != null || spawnLocation == null) return;
        entity = (LivingEntity) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.PLAYER);
        entity.setCustomName(profile.getName());
        entity.setCustomNameVisible(true);
    }

    public void remove() {
        if (entity != null) { entity.remove(); entity = null; }
    }

    public void setEquipment(@Nonnull EquipmentSlot slot, @Nullable ItemStack item) {
        if (entity != null) entity.getEquipment().setItem(slot, item);
    }

    public void addBehaviour(@Nonnull NPCBehaviour behaviour) { behaviours.add(behaviour); }

    public void removeBehaviour(@Nonnull Class<? extends NPCBehaviour> type) {
        behaviours.removeIf(b -> type.isInstance(b));
    }

    @Nonnull
    public List<NPCBehaviour> getBehaviours() { return Collections.unmodifiableList(behaviours); }

    public void setInteractHandler(@Nullable BiConsumer<Player, org.bukkit.event.inventory.ClickType> handler) {
        this.interactHandler = handler;
    }

    public void tick() {
        if (entity == null || entity.isDead()) return;
        for (NPCBehaviour behaviour : behaviours) behaviour.tick(this);
    }

    public void handleInteract(@Nonnull Player player) {
        for (NPCBehaviour behaviour : behaviours) behaviour.onInteract(this, player);
        if (interactHandler != null) interactHandler.accept(player, org.bukkit.event.inventory.ClickType.RIGHT);
    }
}
