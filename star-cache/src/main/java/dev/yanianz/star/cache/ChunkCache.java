package dev.yanianz.star.cache;

import org.bukkit.Chunk;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ChunkCache<V> {
    private final Cache<Long, V> cache;

    public ChunkCache(@Nonnull Cache<Long, V> cache) {
        this.cache = cache;
    }

    @Nullable
    public V get(@Nonnull Chunk chunk) {
        return cache.get(chunk.getChunkKey());
    }

    public void put(@Nonnull Chunk chunk, @Nonnull V value) {
        cache.put(chunk.getChunkKey(), value);
    }

    public void invalidate(@Nonnull Chunk chunk) {
        cache.invalidate(chunk.getChunkKey());
    }
}
