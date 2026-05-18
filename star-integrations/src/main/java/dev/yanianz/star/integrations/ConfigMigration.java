package dev.yanianz.star.integrations;

import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface ConfigMigration {

    void migrate(@Nonnull YamlConfiguration config);
}
