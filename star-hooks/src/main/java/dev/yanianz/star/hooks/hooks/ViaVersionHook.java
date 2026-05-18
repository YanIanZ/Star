package dev.yanianz.star.hooks.hooks;
import dev.yanianz.star.hooks.Hook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import javax.annotation.Nonnull;

public final class ViaVersionHook implements Hook {
    private boolean enabled;

    @Override @Nonnull public String getName() { return "ViaVersion"; }
    @Override public boolean isPresent() { return Bukkit.getPluginManager().getPlugin("ViaVersion") != null; }
    @Override public void enable() { enabled = true; }
    @Override public void disable() { enabled = false; }

    public int getProtocolVersion(@Nonnull Player player) { return enabled ? 0 : -1; }
}
