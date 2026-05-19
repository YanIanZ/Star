package dev.yanianz.star.profiles.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dev.yanianz.star.profiles.Profile;
import dev.yanianz.star.redis.RedisCache;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class CachedProfileRepository implements ProfileRepository {
    private final ProfileRepository delegate;
    private final RedisCache<String> cache;
    private final int ttlSeconds;
    private static final Gson GSON = createGson();
    private static final Type PROFILE_LIST_TYPE = new TypeToken<List<Profile>>() {}.getType();

    public CachedProfileRepository(@Nonnull ProfileRepository delegate, @Nonnull RedisCache<String> cache, int ttlSeconds) {
        this.delegate = delegate;
        this.cache = cache;
        this.ttlSeconds = ttlSeconds;
    }

    static Gson createGson() {
        return new GsonBuilder()
            .registerTypeAdapter(Location.class, new TypeAdapter<Location>() {
                @Override public void write(JsonWriter out, Location value) throws IOException { out.nullValue(); }
                @Override public Location read(JsonReader in) throws IOException { in.nextNull(); return null; }
            })
            .registerTypeAdapter(World.class, new TypeAdapter<World>() {
                @Override public void write(JsonWriter out, World value) throws IOException { out.nullValue(); }
                @Override public World read(JsonReader in) throws IOException { in.nextNull(); return null; }
            })
            .registerTypeAdapter(ItemStack.class, new TypeAdapter<ItemStack>() {
                @Override public void write(JsonWriter out, ItemStack value) throws IOException { out.nullValue(); }
                @Override public ItemStack read(JsonReader in) throws IOException { in.nextNull(); return null; }
            })
            .serializeNulls()
            .create();
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
