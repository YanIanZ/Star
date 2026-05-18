package dev.yanianz.star.world;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public final class RegionSelector implements Listener {
    private final Map<UUID, Location> pos1 = new HashMap<>();
    private final Map<UUID, Location> pos2 = new HashMap<>();
    private final Material wandMaterial;
    private final Plugin plugin;

    public RegionSelector(@Nonnull Plugin plugin, @Nonnull Material wandMaterial) {
        this.plugin = plugin;
        this.wandMaterial = wandMaterial;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public RegionSelector(@Nonnull Plugin plugin) {
        this(plugin, Material.WOODEN_AXE);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() == null || event.getItem().getType() != wandMaterial) return;
        Player p = event.getPlayer();
        Location loc = event.getClickedBlock() != null ? event.getClickedBlock().getLocation() : p.getLocation();
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            pos1.put(p.getUniqueId(), loc);
            p.sendMessage("Position 1 set (" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")");
            event.setCancelled(true);
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            pos2.put(p.getUniqueId(), loc);
            p.sendMessage("Position 2 set (" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")");
            event.setCancelled(true);
        }
    }

    @Nullable
    public CuboidRegion getSelection(@Nonnull Player player) {
        Location p1 = pos1.get(player.getUniqueId());
        Location p2 = pos2.get(player.getUniqueId());
        if (p1 == null || p2 == null) return null;
        if (!p1.getWorld().equals(p2.getWorld())) return null;
        return new CuboidRegion(p1, p2);
    }

    public void clearSelection(@Nonnull Player player) {
        pos1.remove(player.getUniqueId());
        pos2.remove(player.getUniqueId());
    }

    public void giveWand(@Nonnull Player player) {
        player.getInventory().addItem(new ItemStack(wandMaterial));
    }
}
