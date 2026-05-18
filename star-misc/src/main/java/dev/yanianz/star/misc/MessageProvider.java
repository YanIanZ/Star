package dev.yanianz.star.misc;

import org.bukkit.entity.Player;
import javax.annotation.Nonnull;

public final class MessageProvider {
    private final LocaleManager localeManager;

    public MessageProvider(@Nonnull LocaleManager localeManager) { this.localeManager = localeManager; }

    public void send(@Nonnull Player player, @Nonnull String key, @Nonnull String... placeholders) {
        player.sendMessage(localeManager.get(player, key, placeholders));
    }
}
