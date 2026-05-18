package dev.yanianz.star.cache;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface CacheLoader<K, V> {
    V load(@Nonnull K key) throws Exception;
}
