package dev.yanianz.star.redis;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

@DisplayName("Redis")
class TestRedis {
    @Test @DisplayName("RedisConfig with password and database")
    void configWith() {
        RedisConfig cfg = RedisConfig.defaults("localhost").withPassword("secret").withDatabase(2);
        assertEquals("localhost", cfg.host());
        assertEquals(6379, cfg.port());
        assertEquals("secret", cfg.password());
        assertEquals(2, cfg.database());
    }

    @Test @DisplayName("RedisConfig defaults")
    void configDefaults() {
        RedisConfig cfg = RedisConfig.of("redis.example.com", 6380);
        assertEquals("redis.example.com", cfg.host());
        assertEquals(6380, cfg.port());
        assertEquals("", cfg.password());
    }

    @Test @DisplayName("RedisCache namespace")
    void cacheNamespace() {
        RedisCache<String> cache = new RedisCache<>(null, "players");
        assertEquals("players", cache.getNamespace());
    }

    @Test @DisplayName("RedisPubSub channels")
    void pubSub() {
        RedisPubSub ps = new RedisPubSub(null);
        ps.subscribe("test", msg -> {});
        assertEquals(1, ps.getChannels().size());
    }

    @Test @DisplayName("RedisMessenger server ID")
    void messenger() {
        RedisMessenger msg = new RedisMessenger(null, "lobby1");
        assertEquals("lobby1", msg.getServerId());
    }

    @Test @DisplayName("RedisManager config access")
    void managerConfig() {
        RedisConfig cfg = RedisConfig.defaults("localhost");
        RedisManager mgr = new RedisManager(null, cfg);
        assertEquals(cfg, mgr.getConfig());
        assertFalse(mgr.isConnected());
    }

    @Test @DisplayName("RedisManager operations API exists")
    void managerOperations() {
        RedisConfig cfg = RedisConfig.defaults("localhost");
        RedisManager mgr = new RedisManager(null, cfg);
        assertFalse(mgr.isConnected());
        assertNull(mgr.get("key"));
        mgr.del("key");
        assertFalse(mgr.exists("key"));
    }
}
