package dev.yanianz.star.redis;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class RedisPubSub {
    private final RedisManager redis;
    private final Map<String, List<Consumer<String>>> handlers = new ConcurrentHashMap<>();

    public RedisPubSub(@Nonnull RedisManager redis) { this.redis = redis; }

    public void subscribe(@Nonnull String channel, @Nonnull Consumer<String> handler) {
        handlers.computeIfAbsent(channel, k -> new ArrayList<>()).add(handler);
        if (redis != null) {
            redis.subscribe(channel, msg -> handlers.getOrDefault(channel, List.of()).forEach(h -> h.accept(msg)));
        }
    }

    public void publish(@Nonnull String channel, @Nonnull String message) { redis.publish(channel, message); }
    @Nonnull public Set<String> getChannels() { return handlers.keySet(); }
}
