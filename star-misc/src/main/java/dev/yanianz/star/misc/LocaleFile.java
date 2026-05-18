package dev.yanianz.star.misc;

import org.bukkit.configuration.file.YamlConfiguration;
import javax.annotation.Nonnull;
import java.io.File;
import java.util.*;

public final class LocaleFile {
    private final Map<String, String> messages = new HashMap<>();
    private final String code;

    public LocaleFile(@Nonnull File file) throws Exception {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        this.code = yaml.getString("locale.code", "en");
        for (String key : yaml.getKeys(true))
            if (!key.equals("locale")) messages.put(key, yaml.getString(key));
    }

    @Nonnull
    public String getCode() { return code; }

    @Nonnull
    public Map<String, String> getMessages() { return Collections.unmodifiableMap(messages); }

    @Nonnull
    public String get(@Nonnull String key, @Nonnull String defaultValue) { return messages.getOrDefault(key, defaultValue); }
}
