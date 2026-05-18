package dev.yanianz.star.integrations;

import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class PlaceholderExpansion {

    private final String identifier;
    private final String author;
    private final String version;
    private final Map<String, PlaceholderProvider> providers = new LinkedHashMap<>();

    public PlaceholderExpansion(@Nonnull String identifier, @Nonnull String author, @Nonnull String version) {
        this.identifier = identifier;
        this.author = author;
        this.version = version;
    }

    @Nonnull
    public String getIdentifier() {
        return identifier;
    }

    @Nonnull
    public String getAuthor() {
        return author;
    }

    @Nonnull
    public String getVersion() {
        return version;
    }

    public void register(@Nonnull String placeholder, @Nonnull PlaceholderProvider provider) {
        providers.put(placeholder.toLowerCase(), provider);
    }

    @Nullable
    public String onRequest(@Nullable Player player, @Nonnull String params) {
        PlaceholderProvider provider = providers.get(params.toLowerCase());
        return provider != null ? provider.onRequest(player, params) : null;
    }

    @Nonnull
    public Collection<String> getPlaceholders() {
        return providers.keySet();
    }
}
