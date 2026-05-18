package dev.yanianz.star.redis;

import javax.annotation.Nonnull;

public record RedisConfig(@Nonnull String host, int port, @Nonnull String password, int database, int timeout, int poolSize) {
    public static RedisConfig defaults(@Nonnull String host) { return new RedisConfig(host, 6379, "", 0, 2000, 8); }
    public static RedisConfig of(@Nonnull String host, int port) { return new RedisConfig(host, port, "", 0, 2000, 8); }
    public RedisConfig withPassword(@Nonnull String pw) { return new RedisConfig(host, port, pw, database, timeout, poolSize); }
    public RedisConfig withDatabase(int db) { return new RedisConfig(host, port, password, db, timeout, poolSize); }
}
