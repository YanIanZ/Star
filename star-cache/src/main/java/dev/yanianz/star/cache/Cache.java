package dev.yanianz.star.cache;

import dev.yanianz.star.common.StarLogger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cache<K, V> {
    private final ConcurrentHashMap<K, CacheEntry> map = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();
    private final int maxSize;
    private final long ttlMillis;
    private final EvictionPolicy policy;
    private final CacheStats stats = new CacheStats();
    private final CacheLoader<K, V> loader;
    private final CachePersistence<K, V> persistence;
    private final Logger logger;

    public Cache(int maxSize, long ttlMillis, EvictionPolicy policy, CacheLoader<K, V> loader, CachePersistence<K, V> persistence, Logger logger) {
        this.maxSize = maxSize;
        this.ttlMillis = ttlMillis;
        this.policy = policy;
        this.loader = loader;
        this.persistence = persistence;
        this.logger = logger != null ? logger : Logger.getGlobal();
    }

    @Nullable
    public V get(@Nonnull K key) {
        CacheEntry entry = map.get(key);
        if (entry == null || (ttlMillis > 0 && System.currentTimeMillis() - entry.timestamp > ttlMillis)) {
            stats.recordMiss();
            if (entry != null) map.remove(key);
            if (loader != null) {
                long start = System.nanoTime();
                try {
                    V loaded = loader.load(key);
                    if (loaded != null) {
                        put(key, loaded);
                        stats.recordLoad(System.nanoTime() - start);
                        return loaded;
                    }
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Cache load failed for " + key, e);
                }
            }
            return null;
        }
        stats.recordHit();
        entry.touch();
        return entry.value;
    }

    public void put(@Nonnull K key, @Nonnull V value) {
        if (map.size() >= maxSize) evict();
        map.put(key, new CacheEntry(value, System.currentTimeMillis()));
    }

    public void invalidate(@Nonnull K key) {
        map.remove(key);
        stats.recordEviction();
    }

    public void invalidateAll() {
        map.clear();
    }

    public int size() {
        return map.size();
    }

    public boolean containsKey(@Nonnull K key) {
        return map.containsKey(key);
    }

    @Nonnull
    public Set<K> keys() {
        return Collections.unmodifiableSet(map.keySet());
    }

    @Nonnull
    public CacheStats stats() {
        return stats;
    }

    public void save() {
        if (persistence != null) {
            try {
                persistence.save(new HashMap<>(map.entrySet().stream()
                    .collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue().value), HashMap::putAll)));
            } catch (Exception e) {
                logger.log(Level.WARNING, "Cache save failed", e);
            }
        }
    }

    public void load() {
        if (persistence != null) {
            try {
                Map<K, V> loaded = persistence.load();
                loaded.forEach(this::put);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Cache load failed", e);
            }
        }
    }

    private void evict() {
        if (map.isEmpty()) return;
        K evictKey;
        switch (policy) {
            case LRU -> evictKey = map.entrySet().stream()
                .min(Comparator.comparingLong((Map.Entry<K, CacheEntry> e) -> e.getValue().accessTime)
                    .thenComparingLong(e -> e.getValue().order))
                .orElseThrow().getKey();
            case LFU -> evictKey = map.entrySet().stream()
                .min(Comparator.comparingLong((Map.Entry<K, CacheEntry> e) -> e.getValue().accessCount)
                    .thenComparingLong(e -> e.getValue().order))
                .orElseThrow().getKey();
            case TTL_ONLY -> evictKey = map.entrySet().stream()
                .min(Comparator.comparingLong(e -> e.getValue().timestamp))
                .orElseThrow().getKey();
            default -> evictKey = map.keys().nextElement(); // FIFO
        }
        map.remove(evictKey);
        stats.recordEviction();
    }

    private final class CacheEntry {
        final V value;
        final long timestamp;
        long accessTime;
        long accessCount;
        long order;

        CacheEntry(V value, long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
            this.accessTime = timestamp;
            this.order = sequence.incrementAndGet();
        }

        void touch() {
            accessTime = System.currentTimeMillis();
            accessCount++;
            order = sequence.incrementAndGet();
        }
    }
}
