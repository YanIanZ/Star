package dev.yanianz.star.quests;

import dev.yanianz.star.gui.GuiBuilder;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;

public final class QuestBook {

    private final QuestManager manager;

    public QuestBook(@Nonnull QuestManager manager) {
        this.manager = manager;
    }

    public void open(@Nonnull Player player, @Nonnull Plugin plugin) {
        GuiBuilder.create(4, Component.text("Quest Book"))
            .fill(new ItemStack(Material.GRAY_STAINED_GLASS_PANE))
            .build()
            .open(player, plugin);
    }
}
