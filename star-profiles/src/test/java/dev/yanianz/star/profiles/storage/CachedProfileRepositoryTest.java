package dev.yanianz.star.profiles.storage;

import dev.yanianz.star.profiles.Profile;
import dev.yanianz.star.redis.RedisCache;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("CachedProfileRepository")
class CachedProfileRepositoryTest {

    private static final int TTL = 1800;

    @Test @DisplayName("Cache hit returns cached profile, delegate never called")
    void cacheHit() throws Exception {
        ProfileRepository delegate = mock(ProfileRepository.class);
        RedisCache<String> cache = mock(RedisCache.class);
        UUID uuid = UUID.randomUUID();
        Profile profile = Profile.builder("default").serverId("test").createdAt(1L).build();
        String json = CachedProfileRepository.createGson().toJson(profile);

        when(cache.getRaw(uuid + ":default")).thenReturn(json);
        when(cache.getRaw(uuid + ":__gen__")).thenReturn(null);

        CachedProfileRepository repo = new CachedProfileRepository(delegate, cache, TTL);
        Optional<Profile> result = repo.load(uuid, "default").get();

        assertTrue(result.isPresent());
        assertEquals("default", result.get().getName());
        verify(delegate, never()).load(any(), any());
    }

    @Test @DisplayName("Cache miss falls through to delegate and populates cache")
    void cacheMiss() throws Exception {
        ProfileRepository delegate = mock(ProfileRepository.class);
        RedisCache<String> cache = mock(RedisCache.class);
        UUID uuid = UUID.randomUUID();
        Profile profile = Profile.builder("default").serverId("test").createdAt(1L).build();

        when(cache.getRaw(anyString())).thenReturn(null);
        when(delegate.load(uuid, "default")).thenReturn(CompletableFuture.completedFuture(Optional.of(profile)));

        CachedProfileRepository repo = new CachedProfileRepository(delegate, cache, TTL);
        Optional<Profile> result = repo.load(uuid, "default").get();

        assertTrue(result.isPresent());
        verify(delegate).load(uuid, "default");
        verify(cache).setRaw(eq(uuid + ":default"), anyString(), eq(TTL));
    }

    @Test @DisplayName("Save writes through to delegate and populates cache, invalidates list")
    void saveWriteThrough() throws Exception {
        ProfileRepository delegate = mock(ProfileRepository.class);
        RedisCache<String> cache = mock(RedisCache.class);
        UUID uuid = UUID.randomUUID();
        Profile profile = Profile.builder("default").serverId("test").createdAt(1L).build();

        when(delegate.save(any(), any())).thenReturn(CompletableFuture.completedFuture(null));

        CachedProfileRepository repo = new CachedProfileRepository(delegate, cache, TTL);
        repo.save(uuid, profile).get();

        verify(delegate).save(uuid, profile);
        verify(cache).setRaw(eq(uuid + ":default"), anyString(), eq(TTL));
        verify(cache).del(uuid + ":__all__");
        verify(cache).del(uuid + ":__gen__");
    }

    @Test @DisplayName("LoadAll cache hit returns cached list")
    void loadAllCacheHit() throws Exception {
        ProfileRepository delegate = mock(ProfileRepository.class);
        RedisCache<String> cache = mock(RedisCache.class);
        UUID uuid = UUID.randomUUID();
        List<Profile> profiles = List.of(
            Profile.builder("p1").serverId("test").createdAt(1L).build(),
            Profile.builder("p2").serverId("test").createdAt(2L).build()
        );
        String json = CachedProfileRepository.createGson().toJson(profiles);

        when(cache.getRaw(uuid + ":__all__")).thenReturn(json);

        CachedProfileRepository repo = new CachedProfileRepository(delegate, cache, TTL);
        List<Profile> result = repo.loadAll(uuid).get();

        assertEquals(2, result.size());
        verify(delegate, never()).loadAll(any());
    }

    @Test @DisplayName("LoadAll cache miss delegates and populates cache")
    void loadAllCacheMiss() throws Exception {
        ProfileRepository delegate = mock(ProfileRepository.class);
        RedisCache<String> cache = mock(RedisCache.class);
        UUID uuid = UUID.randomUUID();
        List<Profile> profiles = List.of(Profile.builder("p1").serverId("test").createdAt(1L).build());

        when(cache.getRaw(anyString())).thenReturn(null);
        when(delegate.loadAll(uuid)).thenReturn(CompletableFuture.completedFuture(profiles));

        CachedProfileRepository repo = new CachedProfileRepository(delegate, cache, TTL);
        List<Profile> result = repo.loadAll(uuid).get();

        assertEquals(1, result.size());
        verify(delegate).loadAll(uuid);
        verify(cache).setRaw(eq(uuid + ":__all__"), anyString(), eq(TTL));
    }

    @Test @DisplayName("Delete invalidates individual and list caches")
    void deleteInvalidatesCache() throws Exception {
        ProfileRepository delegate = mock(ProfileRepository.class);
        RedisCache<String> cache = mock(RedisCache.class);
        UUID uuid = UUID.randomUUID();

        when(delegate.delete(any(), any())).thenReturn(CompletableFuture.completedFuture(null));

        CachedProfileRepository repo = new CachedProfileRepository(delegate, cache, TTL);
        repo.delete(uuid, "pvp").get();

        verify(delegate).delete(uuid, "pvp");
        verify(cache).del(uuid + ":pvp");
        verify(cache).del(uuid + ":__all__");
        verify(cache).del(uuid + ":__gen__");
    }

    @Test @DisplayName("DeleteAll drops collection and sets gen marker")
    void deleteAllSetsGenMarker() throws Exception {
        ProfileRepository delegate = mock(ProfileRepository.class);
        RedisCache<String> cache = mock(RedisCache.class);
        UUID uuid = UUID.randomUUID();

        when(delegate.deleteAll(any())).thenReturn(CompletableFuture.completedFuture(null));

        CachedProfileRepository repo = new CachedProfileRepository(delegate, cache, TTL);
        repo.deleteAll(uuid).get();

        verify(delegate).deleteAll(uuid);
        verify(cache).del(uuid + ":__all__");
        verify(cache).setRaw(eq(uuid + ":__gen__"), eq("1"), eq(TTL));
    }

    @Test @DisplayName("Gen marker present causes cache bypass")
    void genMarkerBypassesCache() throws Exception {
        ProfileRepository delegate = mock(ProfileRepository.class);
        RedisCache<String> cache = mock(RedisCache.class);
        UUID uuid = UUID.randomUUID();
        Profile profile = Profile.builder("default").serverId("test").createdAt(1L).build();

        when(cache.getRaw(uuid + ":default")).thenReturn("stale");
        when(cache.getRaw(uuid + ":__gen__")).thenReturn("1");
        when(delegate.load(uuid, "default")).thenReturn(CompletableFuture.completedFuture(Optional.of(profile)));

        CachedProfileRepository repo = new CachedProfileRepository(delegate, cache, TTL);
        Optional<Profile> result = repo.load(uuid, "default").get();

        assertTrue(result.isPresent());
        verify(delegate).load(uuid, "default");
    }
}
