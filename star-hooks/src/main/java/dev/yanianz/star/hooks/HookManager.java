package dev.yanianz.star.hooks;
import dev.yanianz.star.common.StarLogger;
import org.bukkit.plugin.Plugin;
import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public final class HookManager {
    private final Plugin plugin;
    private final StarLogger logger;
    private final Map<String, Hook> hooks = new ConcurrentHashMap<>();

    public HookManager(@Nonnull Plugin plugin) { this.plugin = plugin; this.logger = new StarLogger(plugin.getServer(), "hooks"); }

    public void register(@Nonnull Hook hook) { hooks.put(hook.getName(), hook); }

    public void detectAll() {
        for (Hook hook : hooks.values()) { if (hook.isPresent()) { hook.enable(); logger.log(Level.INFO, "Hooked: " + hook.getName()); } }
    }

    public boolean isEnabled(@Nonnull String name) { Hook hook = hooks.get(name); return hook != null && hook.isPresent(); }

    @SuppressWarnings("unchecked")
    @Nonnull public <T extends Hook> T get(@Nonnull String name) { return (T) hooks.get(name); }

    @Nonnull public Collection<Hook> getAll() { return hooks.values(); }
}
