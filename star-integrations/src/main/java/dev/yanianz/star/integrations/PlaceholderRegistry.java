package dev.yanianz.star.integrations;

import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class PlaceholderRegistry {

    private final Map<String, PlaceholderExpansion> expansions = new LinkedHashMap<>();

    public PlaceholderRegistry(@Nonnull Plugin plugin) {
    }

    public void register(@Nonnull PlaceholderExpansion expansion) {
        expansions.put(expansion.getIdentifier(), expansion);
    }

    @Nonnull
    public Collection<PlaceholderExpansion> getExpansions() {
        return Collections.unmodifiableCollection(expansions.values());
    }
}
