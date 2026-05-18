package dev.yanianz.star.profiles;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;

public final class ProfileLoader {
    private final File dataFolder;

    public ProfileLoader(@Nonnull File dataFolder) { this.dataFolder = dataFolder; }

    public void save(@Nonnull Player player, @Nonnull Profile profile) throws IOException {
        File file = new File(dataFolder, player.getUniqueId() + "_" + profile.getName() + ".yml");
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("name", profile.getName());
        profile.getData().getAll().forEach((k, v) -> yaml.set("data." + k, v));
        profile.getLocation().ifPresent(loc -> {
            yaml.set("location.world", loc.getWorld().getName());
            yaml.set("location.x", loc.getX());
            yaml.set("location.y", loc.getY());
            yaml.set("location.z", loc.getZ());
        });
        yaml.save(file);
    }

    @Nonnull public Profile load(@Nonnull Player player, @Nonnull String name) {
        File file = new File(dataFolder, player.getUniqueId() + "_" + name + ".yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        Profile.Builder builder = Profile.builder(name);
        if (yaml.contains("data")) for (String key : yaml.getConfigurationSection("data").getKeys(false)) builder.data(key, yaml.getString("data." + key));
        return builder.build();
    }
}
