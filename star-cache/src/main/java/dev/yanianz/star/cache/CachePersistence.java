package dev.yanianz.star.cache;

import java.io.IOException;
import java.util.Map;

public interface CachePersistence<K, V> {
    void save(Map<K, V> entries) throws IOException;

    Map<K, V> load() throws IOException;
}
