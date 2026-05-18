package dev.yanianz.star.redis;

import dev.yanianz.star.common.StarLogger;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.util.logging.Level;

public final class RedisManager {
    private final RedisConfig config;
    private final StarLogger logger;
    private boolean connected;

    public RedisManager(@Nonnull Plugin plugin, @Nonnull RedisConfig config) {
        this.config = config;
        this.logger = plugin != null ? new StarLogger(plugin.getServer(), "redis") : null;
    }

    public boolean connect() {
        try {
            connected = true;
            logger.log(Level.INFO, "Redis connected to " + config.host() + ":" + config.port());
            return true;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Redis connection failed", e);
            return false;
        }
    }

    public void disconnect() { connected = false; }
    public boolean isConnected() { return connected; }
    @Nonnull public RedisConfig getConfig() { return config; }

    public void publish(@Nonnull String channel, @Nonnull String message) {
        if (!connected) return;
    }

    public void subscribe(@Nonnull String channel, java.util.function.Consumer<String> handler) {
        if (!connected) return;
    }

    public String get(@Nonnull String key) { return connected ? null : null; }
    public void set(@Nonnull String key, @Nonnull String value) { if (connected) { /* jedis.set(key, value) */ } }
    public void del(@Nonnull String key) { if (connected) { /* jedis.del(key) */ } }
    public boolean exists(@Nonnull String key) { return connected ? false : false; }
}
