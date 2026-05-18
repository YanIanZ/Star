package dev.yanianz.star.integrations;

import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;

public final class ConfigVersioner {

    private ConfigVersioner() {}

    public static int getVersion(@Nonnull File file) {
        return YamlConfiguration.loadConfiguration(file).getInt("config-version", 0);
    }

    public static void setVersion(@Nonnull File file, int version) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("config-version", version);
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
