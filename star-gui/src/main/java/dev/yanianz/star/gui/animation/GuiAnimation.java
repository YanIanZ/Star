package dev.yanianz.star.gui.animation;

import dev.yanianz.star.gui.Gui;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public interface GuiAnimation {

    void start(@Nonnull Gui gui, @Nonnull Player player);

    void stop(@Nonnull Gui gui, @Nonnull Player player);

    boolean isRunning();
}
