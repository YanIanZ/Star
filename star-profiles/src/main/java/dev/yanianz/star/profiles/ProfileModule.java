package dev.yanianz.star.profiles;

import com.mongodb.client.MongoDatabase;
import dev.yanianz.star.common.StarLogger;
import dev.yanianz.star.profiles.storage.CachedProfileRepository;
import dev.yanianz.star.profiles.storage.MongoProfileRepository;
import dev.yanianz.star.profiles.storage.ProfileMigration;
import dev.yanianz.star.profiles.storage.ProfileRepository;
import dev.yanianz.star.redis.RedisCache;
import dev.yanianz.star.redis.RedisManager;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;

public final class ProfileModule {
    private final ProfileManager manager;
    private final ProfileListener listener;
    private final ProfileRepository repository;
    private final ProfileMigration migration;

    public ProfileModule(@Nonnull Plugin plugin, @Nonnull MongoDatabase mongoDb,
                         @Nullable RedisManager redis, @Nonnull File dataFolder,
                         @Nonnull String serverId, int redisTtlSeconds, int autoSaveIntervalSeconds) {
        StarLogger logger = new StarLogger(plugin.getServer(), "profiles");
        MongoProfileRepository mongoRepo = new MongoProfileRepository(mongoDb, serverId, logger);

        ProfileRepository repo = mongoRepo;
        if (redis != null && redis.isConnected()) {
            RedisCache<String> cache = new RedisCache<>(redis, "star:profiles");
            repo = new CachedProfileRepository(mongoRepo, cache, redisTtlSeconds);
        }

        this.repository = repo;
        this.manager = new ProfileManager(plugin, repo);
        this.listener = new ProfileListener(manager, logger);
        this.migration = new ProfileMigration(dataFolder, mongoRepo, serverId, logger);
    }

    @Nonnull public ProfileManager getManager() { return manager; }

    @Nonnull public ProfileListener getListener() { return listener; }

    @Nonnull public ProfileRepository getRepository() { return repository; }

    @Nonnull public ProfileMigration getMigration() { return migration; }

    public void registerListener() {
        var pm = org.bukkit.Bukkit.getPluginManager();
        for (RegisteredListener rl : HandlerList.getRegisteredListeners(pm.getPlugin("star"))) {
            if (rl.getListener() instanceof ProfileListener) return;
        }
        pm.registerEvents(listener, pm.getPlugin("star"));
    }

    public void startAutoSave(int intervalSeconds) { manager.startAutoSave(intervalSeconds); }

    public void shutdown() {
        manager.stopAutoSave();
        HandlerList.unregisterAll(listener);
    }
}
