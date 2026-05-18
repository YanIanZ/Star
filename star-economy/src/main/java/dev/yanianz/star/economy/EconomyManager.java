package dev.yanianz.star.economy;

import dev.yanianz.star.common.StarLogger;
import org.bukkit.plugin.Plugin;
import javax.annotation.Nonnull;
import java.util.*;
import java.util.logging.Level;

/**
 * Manages economy provider registration and auto-detection.
 * Use {@link #registerProvider(EconomyProvider)} to add providers,
 * then {@link #detectAndEnable()} to activate the first available one.
 */
public final class EconomyManager {
    private final List<EconomyProvider> providers = new ArrayList<>();
    private EconomyProvider primaryProvider;
    private final StarLogger logger;

    public EconomyManager(@Nonnull Plugin plugin) {
        this.logger = new StarLogger(plugin.getServer(), "economy");
    }

    public void registerProvider(@Nonnull EconomyProvider provider) {
        providers.add(provider);
        logger.log(Level.INFO, "Registered economy provider: " + provider.getName());
    }

    public void detectAndEnable() {
        for (EconomyProvider provider : providers) {
            if (provider.isEnabled()) {
                primaryProvider = provider;
                logger.log(Level.INFO, "Primary economy provider: " + provider.getName());
                return;
            }
        }
        logger.log(Level.WARNING, "No economy provider found!");
    }

    @Nonnull
    public EconomyProvider getPrimaryProvider() {
        if (primaryProvider == null) {
            throw new IllegalStateException("No economy provider available. Did you call detectAndEnable()?");
        }
        return primaryProvider;
    }

    @Nonnull
    public List<EconomyProvider> getProviders() {
        return List.copyOf(providers);
    }

    @Nonnull
    public Optional<EconomyProvider> getProvider(@Nonnull String name) {
        return providers.stream().filter(p -> p.getName().equalsIgnoreCase(name)).findFirst();
    }
}
