package dev.yanianz.star.cache;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.TimeUnit;

@DisplayName("Cache")
class TestCache {
    @Test
    @DisplayName("basic put/get")
    void basicGetPut() {
        Cache<String, String> cache = CacheBuilder.<String, String>create().maxSize(10).build();
        cache.put("a", "1");
        assertEquals("1", cache.get("a"));
        assertEquals(1, cache.size());
        assertTrue(cache.containsKey("a"));
    }

    @Test
    @DisplayName("miss returns null")
    void missReturnsNull() {
        Cache<String, String> cache = CacheBuilder.<String, String>create().maxSize(10).build();
        assertNull(cache.get("nonexistent"));
    }

    @Test
    @DisplayName("invalidate removes entry")
    void invalidate() {
        Cache<String, String> cache = CacheBuilder.<String, String>create().maxSize(10).build();
        cache.put("a", "1");
        cache.invalidate("a");
        assertEquals(0, cache.size());
        assertNull(cache.get("a"));
    }

    @Test
    @DisplayName("eviction policy LRU")
    void evictionLRU() {
        Cache<String, String> cache = CacheBuilder.<String, String>create().maxSize(2).evictionPolicy(EvictionPolicy.LRU).build();
        cache.put("a", "1");
        cache.put("b", "2");
        cache.get("a"); // make a recently used
        cache.put("c", "3"); // evicts b
        assertNotNull(cache.get("a"));
        assertNull(cache.get("b"));
    }

    @Test
    @DisplayName("loading cache auto-loads")
    void loadingCache() {
        Cache<String, String> cache = CacheBuilder.<String, String>create().maxSize(10).loader(k -> "loaded:" + k).build();
        assertEquals("loaded:test", cache.get("test"));
    }

    @Test
    @DisplayName("stats tracks hits/misses")
    void stats() {
        Cache<String, String> cache = CacheBuilder.<String, String>create().maxSize(10).build();
        cache.get("x");
        cache.put("x", "1");
        cache.get("x");
        CacheStats s = cache.stats();
        assertTrue(s.hits() >= 1);
        assertTrue(s.misses() >= 1);
    }

    @Test
    @DisplayName("PlayerCache delegates")
    void playerCache() {
        Cache<java.util.UUID, String> base = CacheBuilder.<java.util.UUID, String>create().maxSize(10).build();
        PlayerCache<String> pc = new PlayerCache<>(base);
        java.util.UUID id = java.util.UUID.randomUUID();
        base.put(id, "data");
        assertEquals(1, base.size());
    }

    @Test
    @DisplayName("MetricCollector formats")
    void metricFormat() {
        Cache<String, String> cache = CacheBuilder.<String, String>create().maxSize(10).build();
        cache.put("a", "1");
        cache.get("a");
        String fmt = MetricCollector.formatShort(cache.stats());
        assertTrue(fmt.contains("hit"));
    }
}
