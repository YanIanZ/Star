package dev.yanianz.star.quests;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.plugin.Plugin;

public final class QuestTrigger implements Listener {

    private final QuestManager manager;

    public QuestTrigger(QuestManager manager, Plugin plugin) {
        this.manager = manager;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onKill(EntityDeathEvent e) {
        if (e.getEntity().getKiller() != null) {
            manager.progress(e.getEntity().getKiller(), ObjectiveType.KILL, e.getEntityType().name(), 1);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        manager.progress(e.getPlayer(), ObjectiveType.BREAK, e.getBlock().getType().name(), 1);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        manager.progress(e.getPlayer(), ObjectiveType.PLACE, e.getBlock().getType().name(), 1);
    }

    @EventHandler
    public void onCraft(CraftItemEvent e) {
        manager.progress((Player) e.getWhoClicked(), ObjectiveType.CRAFT, e.getRecipe().getResult().getType().name(), 1);
    }
}
