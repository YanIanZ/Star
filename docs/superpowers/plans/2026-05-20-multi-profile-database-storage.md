# Multi-Profile Database Storage Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace YAML `ProfileLoader` with MongoDB + Redis persistent storage per-player collections, auto-migrate existing YAML files.

**Architecture:** `ProfileRepository` interface → `MongoProfileRepository` (one collection per UUID) → `CachedProfileRepository` (Redis read/write-through decorator). `ProfileManager` orchestrates in-memory maps + repository. `ProfileMigration` handles YAML→Mongo on first load. `ProfileListener` hooks join/quit for auto-load/save.

**Tech Stack:** Java 25, Bukkit/Paper API, MongoDB via existing `MongoProvider`, Redis via existing `RedisCache`, MockBukkit + JUnit 5 for tests.

---

### Task 1: Extend Profile model with metadata fields

**Files:**
- Modify: `star-profiles/src/main/java/dev/yanianz/star/profiles/Profile.java`

- [ ] **Step 1: Add fields, constructor, getters, and Builder methods to Profile.java**

Replace the entire file with:

```java
package dev.yanianz.star.profiles;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class Profile {
    private final String name;
    private final ProfileData data;
    private final List<ItemStack> inventory;
    private Location location;
    private final long createdAt;
    private volatile long updatedAt;
    private final String serverId;

    Profile(String name, ProfileData data, List<ItemStack> inventory, Location location,
            long createdAt, long updatedAt, String serverId) {
        this.name = name;
        this.data = data;
        this.inventory = inventory;
        this.location = location;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.serverId = serverId;
    }

    @Nonnull public String getName() { return name; }

    @Nonnull public ProfileData getData() { return data; }

    @Nonnull public List<ItemStack> getInventory() { return inventory; }

    @Nonnull public Optional<Location> getLocation() { return Optional.ofNullable(location); }

    public void setLocation(@Nonnull Location loc) { this.location = loc; }

    public long getCreatedAt() { return createdAt; }

    public long getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    @Nonnull public String getServerId() { return serverId; }

    @Nonnull public static Builder builder(@Nonnull String name) { return new Builder(name); }

    public static final class Builder {
        private final String name;
        private final ProfileData data = new ProfileData();
        private final List<ItemStack> inventory = new ArrayList<>();
        private Location location;
        private long createdAt = System.currentTimeMillis();
        private long updatedAt = System.currentTimeMillis();
        private String serverId = "";

        Builder(String name) { this.name = name; }

        @Nonnull public Builder data(@Nonnull String key, @Nonnull String value) { data.set(key, value); return this; }

        @Nonnull public Builder inventory(@Nonnull ItemStack... items) { Collections.addAll(inventory, items); return this; }

        @Nonnull public Builder location(@Nonnull Location loc) { this.location = loc; return this; }

        @Nonnull public Builder createdAt(long createdAt) { this.createdAt = createdAt; return this; }

        @Nonnull public Builder updatedAt(long updatedAt) { this.updatedAt = updatedAt; return this; }

        @Nonnull public Builder serverId(@Nonnull String serverId) { this.serverId = serverId; return this; }

        @Nonnull public Profile build() { return new Profile(name, data, inventory, location, createdAt, updatedAt, serverId); }
    }
}
```

- [ ] **Step 2: Verify compilation**

```bash
./gradlew :star-profiles:compileJava
```
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Run existing tests to confirm no regression**

```bash
./gradlew :star-profiles:test
```
Expected: all tests pass

- [ ] **Step 4: Commit**

```bash
git add star-profiles/src/main/java/dev/yanianz/star/profiles/Profile.java
git commit -m "feat(profiles): add createdAt, updatedAt, serverId metadata to Profile"
```

---

### Task 2: Create ProfileRepository interface

**Files:**
- Create: `star-profiles/src/main/java/dev/yanianz/star/profiles/storage/ProfileRepository.java`

- [ ] **Step 1: Create storage package directory**

```bash
mkdir -p star-profiles/src/main/java/dev/yanianz/star/profiles/storage
```

- [ ] **Step 2: Write ProfileRepository.java**

```java
package dev.yanianz.star.profiles.storage;

import dev.yanianz.star.profiles.Profile;
import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ProfileRepository {

    @Nonnull
    CompletableFuture<Void> save(@Nonnull UUID playerUuid, @Nonnull Profile profile);

    @Nonnull
    CompletableFuture<Optional<Profile>> load(@Nonnull UUID playerUuid, @Nonnull String profileName);

    @Nonnull
    CompletableFuture<List<Profile>> loadAll(@Nonnull UUID playerUuid);

    @Nonnull
    CompletableFuture<Void> delete(@Nonnull UUID playerUuid, @Nonnull String profileName);

    @Nonnull
    CompletableFuture<Void> deleteAll(@Nonnull UUID playerUuid);
}
```

- [ ] **Step 3: Verify compilation**

```bash
./gradlew :star-profiles:compileJava
```
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
git add star-profiles/src/main/java/dev/yanianz/star/profiles/storage/ProfileRepository.java
git commit -m "feat(profiles): add ProfileRepository interface"
```

---

### Task 3: Implement MongoProfileRepository

**Files:**
- Create: `star-profiles/src/main/java/dev/yanianz/star/profiles/storage/MongoProfileRepository.java`
- Create: `star-profiles/src/test/java/dev/yanianz/star/profiles/storage/MongoProfileRepositoryTest.java`

- [ ] **Step 1: Write MongoProfileRepository.java**

```java
package dev.yanianz.star.profiles.storage;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.ReplaceOptions;
import dev.yanianz.star.common.StarLogger;
import dev.yanianz.star.profiles.Profile;
import dev.yanianz.star.profiles.ProfileData;
import dev.yanianz.star.profiles.ProfileInventory;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class MongoProfileRepository implements ProfileRepository {
    private final MongoDatabase database;
    private final String serverId;
    private final StarLogger logger;
    private static final String COLLECTION_PREFIX = "profiles_";

    public MongoProfileRepository(@Nonnull MongoDatabase database, @Nonnull String serverId, @Nonnull StarLogger logger) {
        this.database = database;
        this.serverId = serverId;
        this.logger = logger;
    }

    @Nonnull private String collectionName(@Nonnull UUID playerUuid) {
        return COLLECTION_PREFIX + playerUuid.toString();
    }

    @Nonnull private MongoCollection<Document> collection(@Nonnull UUID playerUuid) {
        MongoCollection<Document> coll = database.getCollection(collectionName(playerUuid));
        coll.createIndex(new Document("serverId", 1), new IndexOptions().background(true));
        coll.createIndex(new Document("updatedAt", 1), new IndexOptions().background(true));
        return coll;
    }

    @Override @Nonnull
    public CompletableFuture<Void> save(@Nonnull UUID playerUuid, @Nonnull Profile profile) {
        return CompletableFuture.runAsync(() -> {
            profile.setUpdatedAt(System.currentTimeMillis());
            Document doc = profileToDocument(profile);
            collection(playerUuid).replaceOne(
                Filters.eq("_id", profile.getName()),
                doc,
                new ReplaceOptions().upsert(true)
            );
        }).exceptionally(ex -> {
            logger.log(Level.SEVERE, "Failed to save profile " + profile.getName() + " for " + playerUuid, ex);
            return null;
        });
    }

    @Override @Nonnull
    public CompletableFuture<Optional<Profile>> load(@Nonnull UUID playerUuid, @Nonnull String profileName) {
        return CompletableFuture.supplyAsync(() -> {
            Document doc = collection(playerUuid).find(Filters.eq("_id", profileName)).first();
            return doc != null ? Optional.of(documentToProfile(doc)) : Optional.empty();
        }).exceptionally(ex -> {
            logger.log(Level.SEVERE, "Failed to load profile " + profileName + " for " + playerUuid, ex);
            return Optional.empty();
        });
    }

    @Override @Nonnull
    public CompletableFuture<List<Profile>> loadAll(@Nonnull UUID playerUuid) {
        return CompletableFuture.supplyAsync(() ->
            StreamSupport.stream(collection(playerUuid).find().spliterator(), false)
                .map(this::documentToProfile)
                .collect(Collectors.toList())
        ).exceptionally(ex -> {
            logger.log(Level.SEVERE, "Failed to load profiles for " + playerUuid, ex);
            return List.of();
        });
    }

    @Override @Nonnull
    public CompletableFuture<Void> delete(@Nonnull UUID playerUuid, @Nonnull String profileName) {
        return CompletableFuture.runAsync(() ->
            collection(playerUuid).deleteOne(Filters.eq("_id", profileName))
        ).exceptionally(ex -> {
            logger.log(Level.SEVERE, "Failed to delete profile " + profileName + " for " + playerUuid, ex);
            return null;
        });
    }

    @Override @Nonnull
    public CompletableFuture<Void> deleteAll(@Nonnull UUID playerUuid) {
        return CompletableFuture.runAsync(() ->
            collection(playerUuid).drop()
        ).exceptionally(ex -> {
            logger.log(Level.SEVERE, "Failed to delete profiles for " + playerUuid, ex);
            return null;
        });
    }

    private Document profileToDocument(Profile profile) {
        Document doc = new Document();
        doc.put("_id", profile.getName());
        doc.put("name", profile.getName());
        doc.put("serverId", profile.getServerId().isEmpty() ? serverId : profile.getServerId());
        doc.put("createdAt", profile.getCreatedAt());
        doc.put("updatedAt", profile.getUpdatedAt());

        Document dataDoc = new Document();
        profile.getData().getAll().forEach(dataDoc::put);
        doc.put("data", dataDoc);

        List<String> inventoryB64 = new ArrayList<>();
        for (ItemStack item : profile.getInventory()) {
            inventoryB64.add(item != null ? itemStackToBase64(item) : null);
        }
        doc.put("inventory", inventoryB64);

        profile.getLocation().ifPresent(loc -> {
            Document locDoc = new Document();
            locDoc.put("world", loc.getWorld().getName());
            locDoc.put("x", loc.getX());
            locDoc.put("y", loc.getY());
            locDoc.put("z", loc.getZ());
            locDoc.put("yaw", (double) loc.getYaw());
            locDoc.put("pitch", (double) loc.getPitch());
            doc.put("location", locDoc);
        });

        return doc;
    }

    private Profile documentToProfile(Document doc) {
        Profile.Builder builder = Profile.builder(doc.getString("name"))
            .createdAt(doc.getLong("createdAt"))
            .updatedAt(doc.getLong("updatedAt"))
            .serverId(doc.getString("serverId"));

        Document dataDoc = doc.get("data", Document.class);
        if (dataDoc != null) {
            dataDoc.forEach((k, v) -> builder.data(k, v.toString()));
        }

        List<String> inventoryB64 = doc.getList("inventory", String.class);
        if (inventoryB64 != null) {
            for (String b64 : inventoryB64) {
                if (b64 != null) {
                    ItemStack item = itemStackFromBase64(b64);
                    if (item != null) builder.inventory(item);
                }
            }
        }

        Document locDoc = doc.get("location", Document.class);
        if (locDoc != null) {
            World world = Bukkit.getWorld(locDoc.getString("world"));
            if (world != null) {
                builder.location(new Location(world,
                    locDoc.getDouble("x"),
                    locDoc.getDouble("y"),
                    locDoc.getDouble("z"),
                    locDoc.getDouble("yaw", 0.0).floatValue(),
                    locDoc.getDouble("pitch", 0.0).floatValue()));
            }
        }

        return builder.build();
    }

    private static String itemStackToBase64(ItemStack item) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BukkitObjectOutputStream boos = new BukkitObjectOutputStream(baos);
            boos.writeObject(item);
            boos.close();
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            return "";
        }
    }

    private static ItemStack itemStackFromBase64(String base64) {
        try {
            byte[] bytes = Base64.getDecoder().decode(base64);
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            BukkitObjectInputStream bois = new BukkitObjectInputStream(bais);
            return (ItemStack) bois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    public boolean hasProfiles(@Nonnull UUID playerUuid) {
        return database.listCollectionNames()
            .into(new ArrayList<>())
            .contains(collectionName(playerUuid));
    }
}
```

- [ ] **Step 2: Verify compilation**

```bash
./gradlew :star-profiles:compileJava
```
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Write MongoProfileRepositoryTest.java**

```java
package dev.yanianz.star.profiles.storage;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.FindIterable;
import dev.yanianz.star.common.StarLogger;
import dev.yanianz.star.profiles.Profile;
import org.bson.Document;
import org.junit.jupiter.api.*;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("MongoProfileRepository")
class MongoProfileRepositoryTest {
    private static ServerMock server;

    @BeforeAll
    static void setUp() {
        MockBukkit.mock();
        server = MockBukkit.getOrCreateMock();
    }

    @AfterAll
    static void tearDown() {
        MockBukkit.unmock();
    }

    @Test @DisplayName("Has no profiles for unknown player")
    void noProfiles() {
        MongoDatabase db = mock(MongoDatabase.class);
        when(db.listCollectionNames()).thenReturn(mock(com.mongodb.client.ListCollectionsIterable.class));
        when(db.listCollectionNames().into(any())).thenReturn(List.of());
        StarLogger logger = mock(StarLogger.class);
        MongoProfileRepository repo = new MongoProfileRepository(db, "test", logger);
        assertFalse(repo.hasProfiles(UUID.randomUUID()));
    }

    @Test @DisplayName("Load returns empty for missing profile")
    void loadMissing() throws Exception {
        MongoDatabase db = mock(MongoDatabase.class);
        MongoCollection<Document> coll = mock(MongoCollection.class);
        when(db.getCollection(anyString())).thenReturn(coll);
        when(coll.find((org.bson.conversions.Bson) any())).thenReturn(mock(FindIterable.class));
        when(coll.find((org.bson.conversions.Bson) any()).first()).thenReturn(null);
        doNothing().when(coll).createIndex(any(), any());
        StarLogger logger = mock(StarLogger.class);
        MongoProfileRepository repo = new MongoProfileRepository(db, "test", logger);
        Optional<Profile> result = repo.load(UUID.randomUUID(), "default").get();
        assertTrue(result.isEmpty());
    }

    @Test @DisplayName("Save and load roundtrip")
    void saveAndLoad() throws Exception {
        MongoDatabase db = mock(MongoDatabase.class);
        MongoCollection<Document> coll = mock(MongoCollection.class);
        when(db.getCollection(anyString())).thenReturn(coll);
        doNothing().when(coll).createIndex(any(), any());
        StarLogger logger = mock(StarLogger.class);
        MongoProfileRepository repo = new MongoProfileRepository(db, "test", logger);
        Profile profile = Profile.builder("default")
            .data("xp", "100")
            .serverId("test")
            .createdAt(12345L)
            .build();
        UUID uuid = UUID.randomUUID();

        repo.save(uuid, profile).get();
        verify(coll).replaceOne(any(), any(), any());
    }
}
```

- [ ] **Step 4: Run tests**

```bash
./gradlew :star-profiles:test
```
Expected: All tests pass (including new MongoProfileRepositoryTest)

- [ ] **Step 5: Commit**

```bash
git add star-profiles/src/main/java/dev/yanianz/star/profiles/storage/MongoProfileRepository.java \
        star-profiles/src/test/java/dev/yanianz/star/profiles/storage/MongoProfileRepositoryTest.java
git commit -m "feat(profiles): add MongoProfileRepository with collection-per-player"
```

---

### Task 4: Implement CachedProfileRepository (Redis decorator)

**Files:**
- Create: `star-profiles/src/main/java/dev/yanianz/star/profiles/storage/CachedProfileRepository.java`
- Create: `star-profiles/src/test/java/dev/yanianz/star/profiles/storage/CachedProfileRepositoryTest.java`

- [ ] **Step 1: Write CachedProfileRepository.java**

```java
package dev.yanianz.star.profiles.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.yanianz.star.profiles.Profile;
import dev.yanianz.star.redis.RedisCache;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class CachedProfileRepository implements ProfileRepository {
    private final ProfileRepository delegate;
    private final RedisCache<String> cache;
    private final int ttlSeconds;
    private static final Gson GSON = new GsonBuilder().serializeNulls().create();
    private static final Type PROFILE_LIST_TYPE = new TypeToken<List<Profile>>() {}.getType();

    public CachedProfileRepository(@Nonnull ProfileRepository delegate, @Nonnull RedisCache<String> cache, int ttlSeconds) {
        this.delegate = delegate;
        this.cache = cache;
        this.ttlSeconds = ttlSeconds;
    }

    private String cacheKey(UUID playerUuid, String profileName) {
        return playerUuid + ":" + profileName;
    }

    private String listCacheKey(UUID playerUuid) {
        return playerUuid + ":__all__";
    }

    @Override @Nonnull
    public CompletableFuture<Void> save(@Nonnull UUID playerUuid, @Nonnull Profile profile) {
        return delegate.save(playerUuid, profile).thenRun(() -> {
            cache.setRaw(cacheKey(playerUuid, profile.getName()), GSON.toJson(profile), ttlSeconds);
            cache.del(listCacheKey(playerUuid));
        });
    }

    @Override @Nonnull
    public CompletableFuture<Optional<Profile>> load(@Nonnull UUID playerUuid, @Nonnull String profileName) {
        String raw = cache.getRaw(cacheKey(playerUuid, profileName));
        if (raw != null) {
            return CompletableFuture.completedFuture(Optional.of(GSON.fromJson(raw, Profile.class)));
        }
        return delegate.load(playerUuid, profileName).thenApply(opt -> {
            opt.ifPresent(profile ->
                cache.setRaw(cacheKey(playerUuid, profileName), GSON.toJson(profile), ttlSeconds));
            return opt;
        });
    }

    @Override @Nonnull
    public CompletableFuture<List<Profile>> loadAll(@Nonnull UUID playerUuid) {
        String raw = cache.getRaw(listCacheKey(playerUuid));
        if (raw != null) {
            return CompletableFuture.completedFuture(GSON.fromJson(raw, PROFILE_LIST_TYPE));
        }
        return delegate.loadAll(playerUuid).thenApply(profiles -> {
            cache.setRaw(listCacheKey(playerUuid), GSON.toJson(profiles), ttlSeconds);
            return profiles;
        });
    }

    @Override @Nonnull
    public CompletableFuture<Void> delete(@Nonnull UUID playerUuid, @Nonnull String profileName) {
        return delegate.delete(playerUuid, profileName).thenRun(() -> {
            cache.del(cacheKey(playerUuid, profileName));
            cache.del(listCacheKey(playerUuid));
        });
    }

    @Override @Nonnull
    public CompletableFuture<Void> deleteAll(@Nonnull UUID playerUuid) {
        return delegate.deleteAll(playerUuid).thenRun(() ->
            cache.del(listCacheKey(playerUuid))
        );
    }
}
```

- [ ] **Step 2: Verify compilation**

```bash
./gradlew :star-profiles:compileJava
```
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Write CachedProfileRepositoryTest.java**

```java
package dev.yanianz.star.profiles.storage;

import dev.yanianz.star.profiles.Profile;
import dev.yanianz.star.redis.RedisCache;
import dev.yanianz.star.redis.RedisManager;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("CachedProfileRepository")
class CachedProfileRepositoryTest {

    @Test @DisplayName("Cache hit returns cached profile")
    void cacheHit() throws Exception {
        ProfileRepository delegate = mock(ProfileRepository.class);
        RedisCache<String> cache = mock(RedisCache.class);
        UUID uuid = UUID.randomUUID();
        Profile profile = Profile.builder("default").serverId("test").createdAt(1L).build();
        String json = new com.google.gson.Gson().toJson(profile);

        when(cache.getRaw(uuid + ":default")).thenReturn(json);

        CachedProfileRepository repo = new CachedProfileRepository(delegate, cache, 1800);
        Optional<Profile> result = repo.load(uuid, "default").get();

        assertTrue(result.isPresent());
        assertEquals("default", result.get().getName());
        verify(delegate, never()).load(any(), any());
    }

    @Test @DisplayName("Cache miss falls through to delegate")
    void cacheMiss() throws Exception {
        ProfileRepository delegate = mock(ProfileRepository.class);
        RedisCache<String> cache = mock(RedisCache.class);
        UUID uuid = UUID.randomUUID();
        Profile profile = Profile.builder("default").serverId("test").createdAt(1L).build();

        when(cache.getRaw(anyString())).thenReturn(null);
        when(delegate.load(uuid, "default")).thenReturn(CompletableFuture.completedFuture(Optional.of(profile)));

        CachedProfileRepository repo = new CachedProfileRepository(delegate, cache, 1800);
        Optional<Profile> result = repo.load(uuid, "default").get();

        assertTrue(result.isPresent());
        verify(delegate).load(uuid, "default");
    }

    @Test @DisplayName("Save writes through to delegate and cache")
    void saveWriteThrough() throws Exception {
        ProfileRepository delegate = mock(ProfileRepository.class);
        RedisCache<String> cache = mock(RedisCache.class);
        UUID uuid = UUID.randomUUID();
        Profile profile = Profile.builder("default").serverId("test").createdAt(1L).build();

        when(delegate.save(any(), any())).thenReturn(CompletableFuture.completedFuture(null));

        CachedProfileRepository repo = new CachedProfileRepository(delegate, cache, 1800);
        repo.save(uuid, profile).get();

        verify(delegate).save(uuid, profile);
    }
}
```

- [ ] **Step 4: Run tests**

```bash
./gradlew :star-profiles:test
```
Expected: All tests pass

- [ ] **Step 5: Commit**

```bash
git add star-profiles/src/main/java/dev/yanianz/star/profiles/storage/CachedProfileRepository.java \
        star-profiles/src/test/java/dev/yanianz/star/profiles/storage/CachedProfileRepositoryTest.java
git commit -m "feat(profiles): add CachedProfileRepository Redis read/write-through decorator"
```

---

### Task 5: Implement ProfileMigration (YAML → MongoDB)

**Files:**
- Create: `star-profiles/src/main/java/dev/yanianz/star/profiles/storage/ProfileMigration.java`
- Create: `star-profiles/src/test/java/dev/yanianz/star/profiles/storage/ProfileMigrationTest.java`

- [ ] **Step 1: Write ProfileMigration.java**

```java
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
```

- [ ] **Step 2: Verify compilation**

```bash
./gradlew :star-profiles:compileJava
```
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Write ProfileMigrationTest.java**

```java
package dev.yanianz.star.profiles.storage;

import dev.yanianz.star.common.StarLogger;
import org.junit.jupiter.api.*;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.io.File;
import java.nio.file.Files;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("ProfileMigration")
class ProfileMigrationTest {
    private static ServerMock server;

    @BeforeAll
    static void setUp() {
        MockBukkit.mock();
        server = MockBukkit.getOrCreateMock();
    }

    @AfterAll
    static void tearDown() {
        MockBukkit.unmock();
    }

    @Test @DisplayName("Migration returns empty list when no YAML files exist")
    void noFilesToMigrate() throws Exception {
        File tempDir = Files.createTempDirectory("star-profiles-migration-test").toFile();
        tempDir.deleteOnExit();
        MongoProfileRepository repo = mock(MongoProfileRepository.class);
        StarLogger logger = mock(StarLogger.class);
        ProfileMigration migration = new ProfileMigration(tempDir, repo, "test", logger);
        java.util.List<dev.yanianz.star.profiles.Profile> result = migration.migrate(UUID.randomUUID()).get();
        assertTrue(result.isEmpty());
    }
}
```

- [ ] **Step 4: Run tests**

```bash
./gradlew :star-profiles:test
```
Expected: All tests pass

- [ ] **Step 5: Commit**

```bash
git add star-profiles/src/main/java/dev/yanianz/star/profiles/storage/ProfileMigration.java \
        star-profiles/src/test/java/dev/yanianz/star/profiles/storage/ProfileMigrationTest.java
git commit -m "feat(profiles): add ProfileMigration for YAML to MongoDB conversion"
```

---

### Task 6: Refactor ProfileManager to use ProfileRepository

**Files:**
- Modify: `star-profiles/src/main/java/dev/yanianz/star/profiles/ProfileManager.java`

- [ ] **Step 1: Replace ProfileManager with repository-backed version**

```java
package dev.yanianz.star.profiles;

import dev.yanianz.star.common.StarLogger;
import dev.yanianz.star.profiles.storage.ProfileRepository;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public final class ProfileManager {
    private final Plugin plugin;
    private final StarLogger logger;
    private final ProfileRepository repository;
    private final Map<UUID, Map<String, Profile>> playerProfiles = new ConcurrentHashMap<>();
    private final Map<UUID, String> activeProfiles = new ConcurrentHashMap<>();

    public ProfileManager(@Nonnull Plugin plugin, @Nonnull ProfileRepository repository) {
        this.plugin = plugin;
        this.logger = new StarLogger(plugin.getServer(), "profiles");
        this.repository = repository;
    }

    public void save(@Nonnull Player player, @Nonnull Profile profile) {
        playerProfiles.computeIfAbsent(player.getUniqueId(), k -> new ConcurrentHashMap<>()).put(profile.getName(), profile);
        repository.save(player.getUniqueId(), profile);
        logger.log(Level.FINE, "Saved profile " + profile.getName() + " for " + player.getName());
    }

    @Nonnull public Optional<Profile> get(@Nonnull UUID playerUuid, @Nonnull String name) {
        return Optional.ofNullable(playerProfiles.getOrDefault(playerUuid, Map.of()).get(name));
    }

    @Nonnull public Optional<Profile> get(@Nonnull Player player, @Nonnull String name) {
        return get(player.getUniqueId(), name);
    }

    @Nonnull public CompletableFuture<List<Profile>> loadAll(@Nonnull UUID playerUuid) {
        return repository.loadAll(playerUuid).thenApply(profiles -> {
            Map<String, Profile> map = playerProfiles.computeIfAbsent(playerUuid, k -> new ConcurrentHashMap<>());
            for (Profile profile : profiles) {
                map.put(profile.getName(), profile);
            }
            if (!profiles.isEmpty() && !activeProfiles.containsKey(playerUuid)) {
                activeProfiles.put(playerUuid, profiles.get(0).getName());
            }
            logger.log(Level.FINE, "Loaded " + profiles.size() + " profiles for " + playerUuid);
            return profiles;
        });
    }

    @Nonnull public Collection<String> getProfileNames(@Nonnull Player player) {
        return playerProfiles.getOrDefault(player.getUniqueId(), Map.of()).keySet();
    }

    public void switchTo(@Nonnull Player player, @Nonnull String profileName) {
        UUID uuid = player.getUniqueId();
        Optional<Profile> current = getActiveProfile(player)
            .flatMap(name -> get(uuid, name));
        current.ifPresent(prev -> {
            prev.setLocation(player.getLocation());
            repository.save(uuid, prev);
        });

        get(uuid, profileName).ifPresent(profile -> {
            activeProfiles.put(uuid, profileName);
            profile.getInventory().forEach(item -> { if (item != null) player.getInventory().addItem(item); });
            profile.getLocation().ifPresent(player::teleport);
            logger.log(Level.INFO, player.getName() + " switched to profile " + profileName);
        });
    }

    @Nonnull public Optional<String> getActiveProfile(@Nonnull Player player) {
        return Optional.ofNullable(activeProfiles.get(player.getUniqueId()));
    }

    public void delete(@Nonnull Player player, @Nonnull String name) {
        UUID uuid = player.getUniqueId();
        playerProfiles.getOrDefault(uuid, Map.of()).remove(name);
        if (name.equals(activeProfiles.get(uuid))) activeProfiles.remove(uuid);
        repository.delete(uuid, name);
    }

    public int getProfileCount(@Nonnull Player player) {
        return playerProfiles.getOrDefault(player.getUniqueId(), Map.of()).size();
    }

    @Nonnull public CompletableFuture<Void> saveAll(@Nonnull UUID playerUuid) {
        Map<String, Profile> profiles = playerProfiles.get(playerUuid);
        if (profiles == null || profiles.isEmpty()) return CompletableFuture.completedFuture(null);
        return CompletableFuture.allOf(
            profiles.values().stream()
                .map(p -> repository.save(playerUuid, p))
                .toArray(CompletableFuture[]::new)
        );
    }

    public void evict(@Nonnull UUID playerUuid) {
        playerProfiles.remove(playerUuid);
        activeProfiles.remove(playerUuid);
    }
}
```

- [ ] **Step 2: Verify compilation**

```bash
./gradlew :star-profiles:compileJava
```
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Run tests**

```bash
./gradlew :star-profiles:test
```
Expected: All tests pass (may need minor test updates for changed constructor)

- [ ] **Step 4: Update TestProfiles.java to match new ProfileManager constructor**

Update the `profileManager` and `profileManagerNames` test methods in `star-profiles/src/test/java/dev/yanianz/star/profiles/TestProfiles.java`:

Replace the `profileManager` test:
```java
@Test @DisplayName("ProfileManager save and get")
void profileManager() {
    Server server = mock(Server.class);
    when(server.getLogger()).thenReturn(java.util.logging.Logger.getLogger("test"));
    Plugin plugin = mock(Plugin.class);
    when(plugin.getServer()).thenReturn(server);
    dev.yanianz.star.profiles.storage.ProfileRepository repo = mock(dev.yanianz.star.profiles.storage.ProfileRepository.class);
    ProfileManager mgr = new ProfileManager(plugin, repo);
    assertNotNull(mgr);
}
```

Replace the `profileManagerNames` test:
```java
@Test @DisplayName("ProfileManager getProfileNames")
void profileManagerNames() {
    Server server = mock(Server.class);
    when(server.getLogger()).thenReturn(java.util.logging.Logger.getLogger("test"));
    Plugin plugin = mock(Plugin.class);
    when(plugin.getServer()).thenReturn(server);
    dev.yanianz.star.profiles.storage.ProfileRepository repo = mock(dev.yanianz.star.profiles.storage.ProfileRepository.class);
    ProfileManager mgr = new ProfileManager(plugin, repo);
    Player player = mock(Player.class);
    when(player.getUniqueId()).thenReturn(UUID.randomUUID());
    assertEquals(0, mgr.getProfileCount(player));
}
```

- [ ] **Step 5: Run tests after test update**

```bash
./gradlew :star-profiles:test
```
Expected: All tests pass

- [ ] **Step 6: Commit**

```bash
git add star-profiles/src/main/java/dev/yanianz/star/profiles/ProfileManager.java \
        star-profiles/src/test/java/dev/yanianz/star/profiles/TestProfiles.java
git commit -m "feat(profiles): refactor ProfileManager with ProfileRepository, add loadAll/saveAll/evict"
```

---

### Task 7: Create ProfileListener for auto-load/save

**Files:**
- Create: `star-profiles/src/main/java/dev/yanianz/star/profiles/ProfileListener.java`
- Modify: `star-profiles/src/test/java/dev/yanianz/star/profiles/TestProfiles.java`

- [ ] **Step 1: Write ProfileListener.java**

```java
package dev.yanianz.star.profiles;

import dev.yanianz.star.common.StarLogger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.logging.Level;

public final class ProfileListener implements Listener {
    private final ProfileManager manager;
    private final StarLogger logger;

    public ProfileListener(@Nonnull ProfileManager manager, @Nonnull StarLogger logger) {
        this.manager = manager;
        this.logger = logger;
    }

    @EventHandler
    public void onJoin(@Nonnull PlayerJoinEvent event) {
        manager.loadAll(event.getPlayer().getUniqueId()).thenAccept(profiles -> {
            if (profiles.isEmpty()) {
                Profile defaultProfile = Profile.builder("default")
                    .serverId("")
                    .createdAt(System.currentTimeMillis())
                    .build();
                manager.save(event.getPlayer(), defaultProfile);
                logger.log(Level.FINE, "Created default profile for " + event.getPlayer().getName());
            }
            logger.log(Level.FINE, "Loaded " + profiles.size() + " profiles for " + event.getPlayer().getName());
        });
    }

    @EventHandler
    public void onQuit(@Nonnull PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        manager.saveAll(uuid).thenRun(() -> {
            manager.evict(uuid);
            logger.log(Level.FINE, "Saved and evicted profiles for " + event.getPlayer().getName());
        });
    }
}
```

- [ ] **Step 2: Add test for ProfileListener in TestProfiles.java**

Append after the last test method but before the closing `}`:

```java
@Test @DisplayName("ProfileListener is created")
void profileListener() {
    dev.yanianz.star.profiles.storage.ProfileRepository repo = mock(dev.yanianz.star.profiles.storage.ProfileRepository.class);
    ProfileManager mgr = new ProfileManager(mock(Plugin.class), repo);
    StarLogger logger = mock(StarLogger.class);
    ProfileListener listener = new ProfileListener(mgr, logger);
    assertNotNull(listener);
}
```

Add the missing imports at the top of TestProfiles.java:
```java
import dev.yanianz.star.common.StarLogger;
```

- [ ] **Step 3: Verify compilation**

```bash
./gradlew :star-profiles:compileJava
```
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Run tests**

```bash
./gradlew :star-profiles:test
```
Expected: All tests pass

- [ ] **Step 5: Commit**

```bash
git add star-profiles/src/main/java/dev/yanianz/star/profiles/ProfileListener.java \
        star-profiles/src/test/java/dev/yanianz/star/profiles/TestProfiles.java
git commit -m "feat(profiles): add ProfileListener for join auto-load and quit save+evict"
```

---

### Task 8: Add periodic auto-save scheduler

**Files:**
- Modify: `star-profiles/src/main/java/dev/yanianz/star/profiles/ProfileManager.java`

- [ ] **Step 1: Add auto-save method and scheduler to ProfileManager**

Add this field to ProfileManager:
```java
private int autoSaveTaskId = -1;
```

Add these methods after `evict`:
```java
public void startAutoSave(int intervalSeconds) {
    if (autoSaveTaskId != -1) return;
    autoSaveTaskId = plugin.getServer().getScheduler().runTaskTimerAsynchronously(
        plugin,
        () -> {
            for (UUID uuid : playerProfiles.keySet()) {
                saveAll(uuid);
            }
        },
        20L * intervalSeconds,
        20L * intervalSeconds
    ).getTaskId();
    logger.log(Level.INFO, "Auto-save started with interval " + intervalSeconds + "s");
}

public void stopAutoSave() {
    if (autoSaveTaskId != -1) {
        plugin.getServer().getScheduler().cancelTask(autoSaveTaskId);
        autoSaveTaskId = -1;
        logger.log(Level.INFO, "Auto-save stopped");
    }
}
```

- [ ] **Step 2: Verify compilation**

```bash
./gradlew :star-profiles:compileJava
```
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Run tests**

```bash
./gradlew :star-profiles:test
```
Expected: All tests pass

- [ ] **Step 4: Commit**

```bash
git add star-profiles/src/main/java/dev/yanianz/star/profiles/ProfileManager.java
git commit -m "feat(profiles): add periodic auto-save scheduler to ProfileManager"
```

---

### Task 9: Delete ProfileLoader and update build.gradle.kts

**Files:**
- Delete: `star-profiles/src/main/java/dev/yanianz/star/profiles/ProfileLoader.java`
- Modify: `star-profiles/build.gradle.kts`

- [ ] **Step 1: Delete ProfileLoader.java**

```bash
rm star-profiles/src/main/java/dev/yanianz/star/profiles/ProfileLoader.java
```

- [ ] **Step 2: Update build.gradle.kts with new dependencies**

Replace `star-profiles/build.gradle.kts` with:
```kotlin
dependencies {
    compileOnly(project(":star-common"))
    compileOnly(project(":star-database"))
    compileOnly(project(":star-redis"))
    compileOnly(project(":star-cache"))
    compileOnly("org.mongodb:mongodb-driver-sync:5.1.0")
    compileOnly("com.google.code.gson:gson:2.11.0")
    testImplementation(project(":star-common"))
    testImplementation(project(":star-database"))
    testImplementation(project(":star-redis"))
    testImplementation(project(":star-cache"))
}
```

- [ ] **Step 3: Verify compilation**

```bash
./gradlew :star-profiles:compileJava
```
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Run all tests**

```bash
./gradlew :star-profiles:test
```
Expected: All tests pass, no ProfileLoader references remain

- [ ] **Step 5: Commit**

```bash
git add star-profiles/build.gradle.kts
git rm star-profiles/src/main/java/dev/yanianz/star/profiles/ProfileLoader.java
git commit -m "feat(profiles): remove ProfileLoader, add database/redis/gson deps"
```

---

### Task 10: Add wiring hook (ProfileModule) for plugin integration

**Files:**
- Create: `star-profiles/src/main/java/dev/yanianz/star/profiles/ProfileModule.java`
- Modify: `star-profiles/src/test/java/dev/yanianz/star/profiles/TestProfiles.java`

- [ ] **Step 1: Write ProfileModule.java — factory that wires everything**

```java
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
```

- [ ] **Step 2: Add test for ProfileModule in TestProfiles.java**

Append before the closing `}`:
```java
@Test @DisplayName("ProfileModule is created without Redis")
void profileModule() {
    com.mongodb.client.MongoDatabase mongoDb = mock(com.mongodb.client.MongoDatabase.class);
    when(mongoDb.listCollectionNames()).thenReturn(mock(com.mongodb.client.ListCollectionsIterable.class));
    when(mongoDb.listCollectionNames().into(any())).thenReturn(java.util.List.of());
    Server server = mock(Server.class);
    when(server.getLogger()).thenReturn(java.util.logging.Logger.getLogger("test"));
    Plugin plugin = mock(Plugin.class);
    when(plugin.getServer()).thenReturn(server);
    ProfileModule module = new ProfileModule(plugin, mongoDb, null, new File("."), "test", 1800, 300);
    assertNotNull(module.getManager());
    assertNotNull(module.getListener());
}
```

- [ ] **Step 3: Verify compilation**

```bash
./gradlew :star-profiles:compileJava compileTestJava
```
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Run tests**

```bash
./gradlew :star-profiles:test
```
Expected: All tests pass

- [ ] **Step 5: Commit**

```bash
git add star-profiles/src/main/java/dev/yanianz/star/profiles/ProfileModule.java \
        star-profiles/src/test/java/dev/yanianz/star/profiles/TestProfiles.java
git commit -m "feat(profiles): add ProfileModule wiring hook for plugin integration"
```

---

### Task 11: Final verification — full build and edge-case tests

- [ ] **Step 1: Run star-profiles tests**

```bash
./gradlew :star-profiles:test
```
Expected: All tests pass

- [ ] **Step 2: Build the whole project**

```bash
./gradlew build -x test
```
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Verify ProfileLoader is deleted and all new files exist**

```bash
ls star-profiles/src/main/java/dev/yanianz/star/profiles/ProfileLoader.java 2>&1
ls star-profiles/src/main/java/dev/yanianz/star/profiles/storage/
```
Expected: ProfileLoader NOT FOUND. Storage dir shows 5 files (ProfileRepository, MongoProfileRepository, CachedProfileRepository, ProfileMigration, package-info).

- [ ] **Step 4: Commit**

```bash
git add -A
git status
git commit -m "chore(profiles): final verification pass — all tests green"
```
