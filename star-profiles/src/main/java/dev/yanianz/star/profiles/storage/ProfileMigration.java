package dev.yanianz.star.profiles.storage;

import dev.yanianz.star.common.StarLogger;
import dev.yanianz.star.profiles.Profile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public final class ProfileMigration {
    private final File dataFolder;
    private final MongoProfileRepository repository;
    private final String serverId;
    private final StarLogger logger;

    public ProfileMigration(@Nonnull File dataFolder, @Nonnull MongoProfileRepository repository,
                            @Nonnull String serverId, @Nonnull StarLogger logger) {
        this.dataFolder = dataFolder;
        this.repository = repository;
        this.serverId = serverId;
        this.logger = logger;
    }

    @Nonnull
    public CompletableFuture<List<Profile>> migrate(@Nonnull UUID playerUuid) {
        return CompletableFuture.supplyAsync(() -> {
            String prefix = playerUuid + "_";
            String suffix = ".yml";
            File[] files = dataFolder.listFiles((FilenameFilter) (dir, name) ->
                name.startsWith(prefix) && name.endsWith(suffix) && !name.endsWith(".yml.migrated"));

            if (files == null || files.length == 0) {
                logger.log(Level.INFO, "No YAML profiles to migrate for " + playerUuid);
                return List.<Profile>of();
            }

            List<Profile> migrated = new ArrayList<>();
            for (File file : files) {
                try {
                    String profileName = file.getName()
                        .substring(prefix.length())
                        .replace(".yml", "");
                    YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
                    Profile.Builder builder = Profile.builder(profileName)
                        .createdAt(file.lastModified())
                        .updatedAt(System.currentTimeMillis())
                        .serverId(serverId);

                    if (yaml.contains("data")) {
                        for (String key : yaml.getConfigurationSection("data").getKeys(false)) {
                            builder.data(key, yaml.getString("data." + key));
                        }
                    }

                    if (yaml.contains("location.world")) {
                        org.bukkit.World world = Bukkit.getWorld(yaml.getString("location.world"));
                        if (world != null) {
                            builder.location(new Location(world,
                                yaml.getDouble("location.x"),
                                yaml.getDouble("location.y"),
                                yaml.getDouble("location.z")));
                        }
                    }

                    Profile profile = builder.build();
                    repository.save(playerUuid, profile).get();

                    File migratedFile = new File(file.getParent(), file.getName() + ".migrated");
                    file.renameTo(migratedFile);
                    migrated.add(profile);
                    logger.log(Level.INFO, "Migrated profile " + profileName + " for " + playerUuid);
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Failed to migrate file " + file.getName() + " for " + playerUuid, e);
                }
            }
            return migrated;
        });
    }
}
