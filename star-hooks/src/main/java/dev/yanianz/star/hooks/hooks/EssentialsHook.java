package dev.yanianz.star.hooks.hooks;
import dev.yanianz.star.hooks.Hook;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class EssentialsHook implements Hook {
    private boolean enabled;

    @Override @Nonnull public String getName() { return "Essentials"; }
    @Override public boolean isPresent() { return Bukkit.getPluginManager().getPlugin("Essentials") != null; }
    @Override public void enable() { enabled = true; }
    @Override public void disable() { enabled = false; }

    @Nullable public Location getLastLocation(@Nonnull Player player) { return enabled ? player.getLastDeathLocation() : null; }
}
