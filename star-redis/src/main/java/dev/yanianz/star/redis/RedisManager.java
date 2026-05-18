package dev.yanianz.star.redis;

import dev.yanianz.star.common.StarLogger;
import org.bukkit.plugin.Plugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class RedisManager {
    private final RedisConfig config;
    private final Logger logger;
    private JedisPool pool;
    private final Map<String, List<Consumer<String>>> subscribers = new ConcurrentHashMap<>();
    private Thread subThread;

    public RedisManager(@Nonnull Plugin plugin, @Nonnull RedisConfig config) {
        this.config = config;
        this.logger = plugin != null ? new StarLogger(plugin.getServer(), "redis") : null;
    }

    public boolean connect() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(config.poolSize());
        poolConfig.setMaxIdle(config.poolSize());
        String password = config.password().isEmpty() ? null : config.password();
        pool = new JedisPool(poolConfig, config.host(), config.port(), config.timeout(), password, config.database());
        try (Jedis jedis = pool.getResource()) {
            jedis.ping();
            log(Level.INFO, "Redis connected to " + config.host() + ":" + config.port());
            return true;
        } catch (Exception e) {
            log(Level.SEVERE, "Redis connection failed", e);
            disconnect();
            return false;
        }
    }

    public void disconnect() {
        subscribers.forEach((ch, handlers) -> unsubscribe(ch));
        if (subThread != null) {
            subThread.interrupt();
            subThread = null;
        }
        if (pool != null && !pool.isClosed()) pool.close();
    }

    public boolean isConnected() { return pool != null && !pool.isClosed(); }

    @Nonnull
    public RedisConfig getConfig() { return config; }

    @Nullable
    public String get(@Nonnull String key) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.get(key);
        } catch (Exception e) {
            log(Level.WARNING, "Redis GET failed: " + key, e);
            return null;
        }
    }

    public void set(@Nonnull String key, @Nonnull String value) {
        try (Jedis jedis = pool.getResource()) {
            jedis.set(key, value);
        } catch (Exception e) {
            log(Level.WARNING, "Redis SET failed: " + key, e);
        }
    }

    public void expire(@Nonnull String key, int seconds) {
        try (Jedis jedis = pool.getResource()) {
            jedis.expire(key, seconds);
        } catch (Exception e) {
            log(Level.WARNING, "Redis EXPIRE failed: " + key, e);
        }
    }

    public void del(@Nonnull String key) {
        try (Jedis jedis = pool.getResource()) {
            jedis.del(key);
        } catch (Exception e) {
            log(Level.WARNING, "Redis DEL failed: " + key, e);
        }
    }

    public boolean exists(@Nonnull String key) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.exists(key);
        } catch (Exception e) {
            return false;
        }
    }

    @Nonnull
    public Set<String> keys(@Nonnull String pattern) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.keys(pattern);
        } catch (Exception e) {
            return Collections.emptySet();
        }
    }

    public void publish(@Nonnull String channel, @Nonnull String message) {
        try (Jedis jedis = pool.getResource()) {
            jedis.publish(channel, message);
        } catch (Exception e) {
            log(Level.WARNING, "Redis PUBLISH failed", e);
        }
    }

    public void subscribe(@Nonnull String channel, @Nonnull Consumer<String> handler) {
        subscribers.computeIfAbsent(channel, k -> new ArrayList<>()).add(handler);
        ensureSubscriberRunning();
        try {
            new Thread(() -> {
                try (Jedis jedis = pool.getResource()) {
                    jedis.subscribe(new JedisPubSub() {
                        @Override
                        public void onMessage(String ch, String msg) {
                            subscribers.getOrDefault(ch, List.of()).forEach(h -> h.accept(msg));
                        }
                    }, channel);
                } catch (Exception ignored) {}
            }).start();
        } catch (Exception e) {
            log(Level.WARNING, "Redis SUBSCRIBE failed", e);
        }
    }

    private void unsubscribe(String channel) { /* handled by pool close */ }

    private void ensureSubscriberRunning() {}

    public void hset(@Nonnull String key, @Nonnull String field, @Nonnull String value) {
        try (Jedis jedis = pool.getResource()) {
            jedis.hset(key, field, value);
        } catch (Exception e) {
            log(Level.WARNING, "Redis HSET failed", e);
        }
    }

    @Nullable
    public String hget(@Nonnull String key, @Nonnull String field) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.hget(key, field);
        } catch (Exception e) {
            return null;
        }
    }

    @Nonnull
    public Map<String, String> hgetAll(@Nonnull String key) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.hgetAll(key);
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    public long incr(@Nonnull String key) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.incr(key);
        } catch (Exception e) {
            return -1;
        }
    }

    public void sadd(@Nonnull String key, @Nonnull String... members) {
        try (Jedis jedis = pool.getResource()) {
            jedis.sadd(key, members);
        } catch (Exception e) {
            log(Level.WARNING, "Redis SADD failed", e);
        }
    }

    @Nonnull
    public Set<String> smembers(@Nonnull String key) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.smembers(key);
        } catch (Exception e) {
            return Collections.emptySet();
        }
    }

    private void log(Level level, String msg) {
        if (logger != null) logger.log(level, msg);
    }

    private void log(Level level, String msg, Throwable thrown) {
        if (logger != null) logger.log(level, msg, thrown);
    }
}
