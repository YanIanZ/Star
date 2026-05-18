# Design: star-cache + star-misc + star-integrations

**Date:** 2026-05-19 | **Project:** Star v1.4.0

---

## 1. star-cache (13 classes)

Full caching framework: generic `Cache<K,V>`, `LoadingCache`, `PlayerCache`, `ChunkCache`, LRU/LFU/FIFO/TTL eviction, JSON persistence, warmup, metrics, async ops.

| Class | Purpose |
|-------|---------|
| `Cache.java` | Core map-based cache with TTL, max size, eviction |
| `CacheBuilder.java` | Fluent builder for Cache/LoadingCache |
| `CacheStats.java` | Hits, misses, evictions, load time tracking |
| `LoadingCache.java` | Auto-loads on get() miss via CacheLoader |
| `CacheLoader.java` | `V load(K key)` interface |
| `PlayerCache.java` | `Cache<UUID, V>` per-player wrapper |
| `ChunkCache.java` | `Cache<ChunkCoord, V>` chunk-level wrapper |
| `EvictionPolicy.java` | LRU, LFU, FIFO, TTL_ONLY enum |
| `CachePersistence.java` | Save/load interface |
| `JsonCachePersistence.java` | Gson-based disk persistence |
| `CacheWarmup.java` | Preload keys in bulk |
| `MetricCollector.java` | Hit rate, avg load time, eviction rate |
| `AsyncCacheOps.java` | CompletableFuture async get/put |

## 2. star-misc (8 classes)

Sounds and localization utilities.

| Class | Purpose |
|-------|---------|
| `SoundPlayer.java` | Play sounds to player/world/location |
| `SoundBuilder.java` | Fluent builder: pitch, volume, category, delay |
| `MusicPlayer.java` | Looping music with fade in/out |
| `LocaleManager.java` | Per-player locale, load from YAML files |
| `MessageProvider.java` | Key-based message with placeholders |
| `LocaleFile.java` | Parse locale YAML files |
| `PlaceholderResolver.java` | Replace {key} with values |

## 3. star-integrations (6 classes)

PlaceholderAPI + config migration.

| Class | Purpose |
|-------|---------|
| `PlaceholderExpansion.java` | Base expansion class |
| `PlaceholderRegistry.java` | Register + auto-enable expansions |
| `PlaceholderProvider.java` | Provider interface |
| `ConfigMigrator.java` | Version-tracked YAML migration |
| `ConfigVersioner.java` | Config with version tracking |
| `ConfigMigration.java` | Migration step interface |

## 4. Testing
6-8 tests per module, 20+ total.
