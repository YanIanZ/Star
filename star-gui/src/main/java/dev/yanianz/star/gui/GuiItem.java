package dev.yanianz.star.gui;

import org.bukkit.inventory.ItemStack;
import javax.annotation.Nonnull;
import java.util.function.Consumer;

public record GuiItem(@Nonnull ItemStack item, @Nonnull Consumer<GuiClickEvent> handler) {}
