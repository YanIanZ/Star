package dev.yanianz.star.hooks.hooks;
import dev.yanianz.star.hooks.Hook;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import javax.annotation.Nonnull;

public final class WorldGuardHook implements Hook {
    private boolean enabled;

    @Override @Nonnull public String getName() { return "WorldGuard"; }
    @Override public boolean isPresent() { return Bukkit.getPluginManager().getPlugin("WorldGuard") != null; }
    @Override public void enable() { enabled = true; }
    @Override public void disable() { enabled = false; }

    public boolean canBuild(@Nonnull Player player, @Nonnull Location location) { return enabled || true; }
}
