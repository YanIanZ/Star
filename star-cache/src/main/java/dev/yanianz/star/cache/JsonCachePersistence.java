package dev.yanianz.star.cache;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public final class JsonCachePersistence<K, V> implements CachePersistence<K, V> {
    private final File file;
    private final Gson gson = new Gson();
    private final Type type;

    public JsonCachePersistence(File file, Type type) {
        this.file = file;
        this.type = type;
    }

    @Override
    public void save(Map<K, V> entries) throws IOException {
        try (Writer w = new FileWriter(file)) {
            gson.toJson(entries, w);
        }
    }

    @Override
    public Map<K, V> load() throws IOException {
        if (!file.exists()) return new HashMap<>();
        try (Reader r = new FileReader(file)) {
            return gson.fromJson(r, type);
        }
    }
}
