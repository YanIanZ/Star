package dev.yanianz.star.hooks.hooks;
import dev.yanianz.star.hooks.Hook;
import org.bukkit.Bukkit;
import javax.annotation.Nonnull;

public final class DiscordSRVHook implements Hook {
    private boolean enabled;

    @Override @Nonnull public String getName() { return "DiscordSRV"; }
    @Override public boolean isPresent() { return Bukkit.getPluginManager().getPlugin("DiscordSRV") != null; }
    @Override public void enable() { enabled = true; }
    @Override public void disable() { enabled = false; }
}
