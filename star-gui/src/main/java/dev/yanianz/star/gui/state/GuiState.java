package dev.yanianz.star.gui.state;

import dev.yanianz.star.gui.Gui;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public abstract class GuiState<D> {

    protected D data;

    protected GuiState(@Nonnull D initialData) {
        this.data = initialData;
    }

    @Nonnull
    public D getData() {
        return data;
    }

    public void setData(@Nonnull D data) {
        this.data = data;
    }

    @Nonnull
    public abstract Gui build(@Nonnull Player player);
}
