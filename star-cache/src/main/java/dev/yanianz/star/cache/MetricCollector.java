package dev.yanianz.star.cache;

import javax.annotation.Nonnull;

public final class MetricCollector {
    private MetricCollector() {}

    @Nonnull
    public static String format(@Nonnull CacheStats stats) {
        return String.format("hits=%d misses=%d hitRate=%.2f evictions=%d loads=%d avgLoadMs=%.2f",
            stats.hits(), stats.misses(), stats.hitRate(), stats.evictions(), stats.loads(), stats.avgLoadTimeMs());
    }

    @Nonnull
    public static String formatShort(@Nonnull CacheStats stats) {
        return String.format("%.1f%% hit (%d/%d)", stats.hitRate() * 100, stats.hits(), stats.hits() + stats.misses());
    }
}
