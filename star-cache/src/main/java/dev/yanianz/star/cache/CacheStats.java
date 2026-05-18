package dev.yanianz.star.cache;

import java.util.concurrent.atomic.LongAdder;

public final class CacheStats {
    private final LongAdder hits = new LongAdder();
    private final LongAdder misses = new LongAdder();
    private final LongAdder evictions = new LongAdder();
    private final LongAdder loads = new LongAdder();
    private final LongAdder totalLoadTime = new LongAdder();

    public void recordHit() {
        hits.increment();
    }

    public void recordMiss() {
        misses.increment();
    }

    public void recordEviction() {
        evictions.increment();
    }

    public void recordLoad(long nanos) {
        loads.increment();
        totalLoadTime.add(nanos);
    }

    public long hits() {
        return hits.sum();
    }

    public long misses() {
        return misses.sum();
    }

    public long evictions() {
        return evictions.sum();
    }

    public long loads() {
        return loads.sum();
    }

    public double hitRate() {
        long total = hits.sum() + misses.sum();
        return total == 0 ? 0 : (double) hits.sum() / total;
    }

    public double avgLoadTimeMs() {
        long l = loads.sum();
        return l == 0 ? 0 : totalLoadTime.sum() / (double) l / 1_000_000;
    }
}
