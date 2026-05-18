package dev.yanianz.star.redis;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;

public final class RedisMessenger {
    private final RedisManager redis;
    private final String serverId;

    public RedisMessenger(@Nonnull RedisManager redis, @Nonnull String serverId) {
        this.redis = redis;
        this.serverId = serverId;
    }

    public void send(@Nonnull String targetServer, @Nonnull String message) { redis.publish("star:msg:" + targetServer, message); }
    public void broadcast(@Nonnull String message) { redis.publish("star:msg:all", message); }

    public void listen(@Nonnull BiConsumer<String, String> handler) {
        redis.subscribe("star:msg:" + serverId, msg -> handler.accept(serverId, msg));
        redis.subscribe("star:msg:all", msg -> handler.accept("all", msg));
    }

    @Nonnull public String getServerId() { return serverId; }
}
