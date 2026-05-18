package dev.yanianz.star.test;
import java.util.*;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

public final class SchedulerMock {
    private final List<Runnable> pending = new ArrayList<>();
    private int tick;

    public void runTask(@Nonnull Runnable task) { task.run(); }
    public void runTaskLater(@Nonnull Runnable task, long delay) { pending.add(task); }
    public void tick() { tick++; List<Runnable> copy = new ArrayList<>(pending); pending.clear(); copy.forEach(Runnable::run); }
    public void tickTimes(int count) { for (int i = 0; i < count; i++) tick(); }
    public int getTick() { return tick; }
}
