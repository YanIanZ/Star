package dev.yanianz.star.hooks.hooks;
import dev.yanianz.star.hooks.Hook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class LuckPermsHook implements Hook {
    private boolean enabled;

    @Override @Nonnull public String getName() { return "LuckPerms"; }
    @Override public boolean isPresent() { return Bukkit.getPluginManager().getPlugin("LuckPerms") != null; }
    @Override public void enable() { enabled = true; }
    @Override public void disable() { enabled = false; }

    @Nullable public String getPrefix(@Nonnull Player player) { return enabled ? getMeta(player, "prefix") : null; }
    @Nullable public String getSuffix(@Nonnull Player player) { return enabled ? getMeta(player, "suffix") : null; }
    @Nullable public String getMeta(@Nonnull Player player, @Nonnull String key) { return null; /* requires LuckPerms API */ }
    @Nullable public String getGroup(@Nonnull Player player) { return enabled ? "default" : null; }
}
