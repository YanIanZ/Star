# Multi-Profile Database Storage — Design Spec

**Date:** 2026-05-20  
**Decision:** Move multi-profile storage from YAML files to MongoDB + Redis, one collection per player.

## §G — Goal

Replace YAML-based `ProfileLoader` with MongoDB-backed persistent storage, Redis caching layer, and per-player collection isolation. Auto-migrate existing YAML profiles on first load.

## §C — Non-Goals

- No swappable backend abstraction (MySQL/SQLite) — user explicitly chose MongoDB
- No cross-server profile sync beyond what Redis and shared MongoDB provide
- No in-game GUI or command changes beyond `/profile` staying functional

## §I — Architecture

```
star-profiles (new deps: star-database, star-redis, star-cache)
├── model/
│   ├── Profile.java              — extended: createdAt, updatedAt, serverId
│   ├── ProfileData.java          — unchanged (LinkedHashMap<String,String>)
│   └── ProfileInventory.java     — unchanged (inventory snapshot/restore)
├── storage/
│   ├── ProfileRepository.java    — interface: save/load/delete/list/getAll
│   ├── MongoProfileRepository.java — MongoDB, collection "profiles_<uuid>" per player
│   ├── CachedProfileRepository.java — Redis decorator wrapping Mongo (read-through/write-through)
│   └── ProfileMigration.java     — scans YAML, inserts into Mongo, renames originals
├── ProfileManager.java           — orchestrator: in-memory ConcurrentHashMap + repository
├── ProfileListener.java          — NEW: PlayerJoinEvent load, PlayerQuitEvent save
└── ProfileCommand.java           — unchanged, delegates to ProfileManager

DELETED:
  ProfileLoader.java — replaced by ProfileRepository + ProfileMigration
```

### Component Map

| Component | Role | Depends on |
|-----------|------|-----------|
| `ProfileRepository` | Interface defining persistence contract | — |
| `MongoProfileRepository` | CRUD via MongoDB, one collection/player | `MongoDatabase`, `MongoCollection` |
| `CachedProfileRepository` | Redis read-through / write-through cache | `RedisCache`, `ProfileRepository` |
| `ProfileMigration` | YAML → MongoDB one-shot migration | `MongoProfileRepository`, `YamlConfiguration` |
| `ProfileManager` | Sync in-memory state, delegate to repository | `ProfileRepository`, `Profile`, `ProfileInventory` |
| `ProfileListener` | Auto-load on join, auto-save on quit | `ProfileManager` |

## §D — Data Model

### Profile.java — extended fields

```java
public record Profile(
    String name,
    ProfileData data,                          // LinkedHashMap<String,String>
    List<ItemStack> inventory,                 // serialized as base64 in Mongo
    Optional<Location> location,               // world, x, y, z, yaw, pitch
    long createdAt,                            // epoch millis
    long updatedAt,                            // epoch millis
    String serverId                            // server identifier for multi-server
) {}
```

### MongoDB Document Schema (per profile, in collection `profiles_<uuid>`)

```json
{
  "_id": "profile_name",
  "name": "default",
  "serverId": "lobby-01",
  "createdAt": 1747728000000,
  "updatedAt": 1747728000000,
  "data": {
    "health": "20",
    "level": "5"
  },
  "inventory": ["base64_item_1", "base64_item_2"],
  "location": {
    "world": "world",
    "x": 100.5,
    "y": 64.0,
    "z": -200.3,
    "yaw": 90.0,
    "pitch": 0.0
  }
}
```

### Indexes

| Field | Type | Purpose |
|-------|------|---------|
| `_id` | default unique | Profile name lookup |
| `serverId` | regular index | Multi-server queries |
| `updatedAt` | regular index | Staleness / cleanup queries |

## §F — Data Flow

### Player Join
```
PlayerJoinEvent
  → ProfileListener.onJoin()
  → ProfileManager.loadAll(playerUuid)
  → CachedProfileRepository.getAll(playerUuid)
    → Redis HIT → return cached profiles (deserialize JSON → Profile)
    → Redis MISS → MongoProfileRepository.getAll(playerUuid)
      → finds collection "profiles_<uuid>", fetch all documents
      → if collection empty/missing → ProfileMigration.migrate(player)
        → scan dataFolder for <uuid>_*.yml, parse, insert, rename to .migrated
      → serialize → store in Redis (TTL 30 min)
  → populate ProfileManager.playerProfiles in-memory
  → auto-load last active / default profile
```

### Profile Switch
```
ProfileManager.switchTo(player, "pvp")
  → save CURRENT active profile to repository (async CompletableFuture)
  → load target profile from in-memory (must be already cached)
  → restore inventory (ProfileInventory.restore)
  → teleport to location (if present)
  → update activeProfiles map
```

### Player Quit
```
PlayerQuitEvent
  → ProfileListener.onQuit()
  → ProfileManager.saveAll(playerUuid)
  → for each profile in playerProfiles[uuid]:
    → repository.save(profile)  →  Mongo insert/replace + Redis set
  → evict from in-memory maps
  → optionally flush Redis entries
```

### Auto-Save (periodic)
```
Bukkit.getScheduler().runTaskTimerAsync()
  → every N seconds (config: auto-save-interval-seconds)
  → iterate dirty profiles (tracked via dirty flag on Profile or via separate set)
  → repository.save(dirtyProfiles)
  → clear dirty flags
```

## §R — Redis Caching Strategy

| Aspect | Decision |
|--------|----------|
| Key pattern | `star:profiles:<uuid>:<profileName>` |
| Value | JSON-serialized Profile |
| TTL | Configurable, default 1800 seconds (30 min) |
| Write policy | Write-through (Mongo + Redis on every save) |
| Read policy | Read-through (Redis first, Mongo fallback, populate Redis) |
| On quit | Evict all player entries from Redis |
| Module | Uses existing `RedisCache` from `star-redis` |

## §M — Migration (YAML → MongoDB)

### Trigger
`MongoProfileRepository.getAll(uuid)` returns empty/no collection → invoke `ProfileMigration.migrate(player)`

### Steps
1. Scan `<dataFolder>/` for files matching `<uuid>_<profileName>.yml`
2. For each file:
   - Parse with Bukkit `YamlConfiguration`
   - Build Profile document: copy name, data, inventory, location
   - Set `createdAt` = file.lastModified(), `updatedAt` = System.currentTimeMillis(), `serverId` = programmatic server ID
   - Insert into `profiles_<uuid>` collection
3. Rename all successfully migrated files to `*.yml.migrated` (keeps backup)
4. Return list of migrated profiles for loading

### Error handling
- Any parse/insert failure → skip file, log error, keep `.yml` untouched
- Partial failure → drop collection if no documents inserted yet, keep YAML files

## §Cg — Configuration

```yaml
# In star's main config
profiles:
  storage: mongodb
  redis-cache: true
  redis-ttl-minutes: 30
  default-profile: "default"
  auto-save-interval-seconds: 300
  server-id: "lobby-01"
```

## §E — Error Handling

| Scenario | Action |
|----------|--------|
| MongoDB unreachable | Log WARN, operate in-memory only, enqueue dirty profiles for retry |
| Redis unreachable | Log WARN, bypass cache, direct Mongo reads/writes |
| Corrupted YAML in migration | Skip file, log ERROR, keep original file |
| Invalid/corrupt Mongo document | Skip document, log WARN, continue loading others |
| Concurrent switch during save | Synchronize on `playerUuid` interned String, queue save after switch |
| No profiles exist for player | Auto-create "default" profile on first join |

## §T — Tasks

| ID | Task | Depends on | Verification |
|----|------|------------|-------------|
| T.1 | Extend `Profile.java` with `createdAt`, `updatedAt`, `serverId` fields | — | Compile + existing tests pass |
| T.2 | Create `ProfileRepository` interface (save/load/delete/list/getAll) | T.1 | Compile |
| T.3 | Implement `MongoProfileRepository` (collection-per-player CRUD) | T.2 | Unit test: insert, read, update, delete |
| T.4 | Implement `CachedProfileRepository` (Redis read-through/write-through) | T.3 | Unit test: cache hit, cache miss, write-through |
| T.5 | Implement `ProfileMigration` (YAML scan → Mongo insert → rename) | T.3 | Unit test: mock YAML file, verify Mongo inserts |
| T.6 | Refactor `ProfileManager` to use `ProfileRepository` (persistent save/load) | T.4, T.5 | Unit test: save/load roundtrip |
| T.7 | Create `ProfileListener` (join → load, quit → save) | T.6 | Integration test: mock join/quit events |
| T.8 | Add periodic auto-save scheduler | T.6 | Verify dirty profiles flushed on interval |
| T.9 | Delete `ProfileLoader.java` | T.5 | Compile succeeds without it |
| T.10 | Update `star-profiles/build.gradle.kts` with new dependencies | T.3 | Gradle sync passes |
| T.11 | Add profiles config section, wiring | T.10 | Config loads, MongoDB connects |

## §V — Invariants

| ID | Invariant |
|----|-----------|
| V.1 | ProfileManager in-memory state always matches MongoDB + Redis after save completes |
| V.2 | Each player's profiles live in exactly one MongoDB collection: `profiles_<uuid>` |
| V.3 | Migration runs at most once per player (`.yml.migrated` files prevent re-run) |
| V.4 | Profile switch must complete current save before loading target (no partial states) |
| V.5 | All repository writes are async (CompletableFuture), non-blocking on main thread |

## §B — Backprop (bugs found post-spec)

| ID | Bug | Fix | Date |
|----|-----|-----|------|
| — | — | — | — |
