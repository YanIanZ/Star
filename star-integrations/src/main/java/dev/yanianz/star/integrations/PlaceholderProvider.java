package dev.yanianz.star.integrations;

import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@FunctionalInterface
public interface PlaceholderProvider {

    @Nullable
    String onRequest(@Nullable Player player, @Nonnull String params);
}
