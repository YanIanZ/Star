package dev.yanianz.star.hooks.hooks;
import dev.yanianz.star.hooks.Hook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import javax.annotation.Nonnull;

public final class PlaceholderAPIHook implements Hook {
    private boolean enabled;

    @Override @Nonnull public String getName() { return "PlaceholderAPI"; }
    @Override public boolean isPresent() { return Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null; }
    @Override public void enable() { enabled = true; }
    @Override public void disable() { enabled = false; }

    @Nonnull public String set(@Nonnull Player player, @Nonnull String text) { return enabled ? text : text; }
}
