package dev.yanianz.star.profiles;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public final class ProfileData {
    private final Map<String, String> data = new LinkedHashMap<>();

    public void set(@Nonnull String key, @Nonnull String value) { data.put(key, value); }

    @Nullable public String get(@Nonnull String key) { return data.get(key); }

    @Nonnull public String getOrDefault(@Nonnull String key, @Nonnull String def) { return data.getOrDefault(key, def); }

    public int getInt(@Nonnull String key) { return Integer.parseInt(data.getOrDefault(key, "0")); }

    public double getDouble(@Nonnull String key) { return Double.parseDouble(data.getOrDefault(key, "0")); }

    public boolean getBoolean(@Nonnull String key) { return Boolean.parseBoolean(data.get(key)); }

    @Nonnull public Map<String, String> getAll() { return Map.copyOf(data); }

    public boolean has(@Nonnull String key) { return data.containsKey(key); }
}
