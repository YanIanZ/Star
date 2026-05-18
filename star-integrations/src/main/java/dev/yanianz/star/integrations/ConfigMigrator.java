package dev.yanianz.star.integrations;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public final class ConfigMigrator {

    private final File configFile;
    private final Map<Integer, ConfigMigration> migrations = new TreeMap<>();

    public ConfigMigrator(@Nonnull Plugin plugin, @Nonnull File configFile) {
        this.configFile = configFile;
    }

    public void addVersion(int version, @Nonnull ConfigMigration migration) {
        migrations.put(version, migration);
    }

    public void migrate() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        int currentVersion = config.getInt("config-version", 0);

        for (Map.Entry<Integer, ConfigMigration> e : migrations.entrySet()) {
            if (e.getKey() > currentVersion) {
                e.getValue().migrate(config);
                config.set("config-version", e.getKey());
            }
        }

        try {
            config.save(configFile);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to save migrated config", ex);
        }
    }
}
