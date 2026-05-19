package dev.yanianz.star.profiles.storage;

import dev.yanianz.star.profiles.Profile;
import dev.yanianz.star.redis.RedisCache;
import org.junit.jupiter.api.*;

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
        String json = CachedProfileRepository.createGson().toJson(profile);

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
