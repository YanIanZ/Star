package dev.yanianz.star.holograms;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class HologramLine {
    private Component text;
    private ItemStack item;

    public HologramLine(@Nonnull Component text) { this.text = text; }
    public HologramLine(@Nonnull ItemStack item) { this.item = item; }

    public boolean isText() { return text != null; }
    public boolean isItem() { return item != null; }
    @Nullable public Component getText() { return text; }
    @Nullable public ItemStack getItem() { return item; }
    public void setText(@Nonnull Component text) { this.text = text; this.item = null; }
    public void setItem(@Nonnull ItemStack item) { this.item = item; this.text = null; }
}
