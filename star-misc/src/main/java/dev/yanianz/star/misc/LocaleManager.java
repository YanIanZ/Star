package dev.yanianz.star.misc;

import dev.yanianz.star.common.StarLogger;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import javax.annotation.Nonnull;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public final class LocaleManager {
    private final Plugin plugin;
    private final StarLogger logger;
    private final Map<String, LocaleFile> locales = new HashMap<>();
    private final Map<UUID, String> playerLocales = new ConcurrentHashMap<>();
    private String defaultCode;

    public LocaleManager(@Nonnull Plugin plugin, @Nonnull String defaultCode) {
        this.plugin = plugin;
        this.logger = new StarLogger(plugin.getServer(), "locale");
        this.defaultCode = defaultCode;
    }

    public void load(@Nonnull String filename) {
        File file = new File(plugin.getDataFolder(), filename);
        try {
            LocaleFile locale = new LocaleFile(file);
            locales.put(locale.getCode(), locale);
            logger.log(Level.INFO, "Loaded locale: " + locale.getCode());
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to load locale: " + filename, e);
        }
    }

    public void setLocale(@Nonnull Player player, @Nonnull String code) { playerLocales.put(player.getUniqueId(), code); }

    @Nonnull
    public String get(@Nonnull Player player, @Nonnull String key, @Nonnull String... placeholders) {
        String code = playerLocales.getOrDefault(player.getUniqueId(), defaultCode);
        LocaleFile locale = locales.get(code);
        String msg = locale != null ? locale.get(key, key) : key;
        if (placeholders.length % 2 == 0) {
            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < placeholders.length; i += 2) map.put(placeholders[i], placeholders[i + 1]);
            return PlaceholderResolver.resolve(msg, map);
        }
        return msg;
    }

    @Nonnull
    public Collection<String> getAvailableCodes() { return locales.keySet(); }
}
