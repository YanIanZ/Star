package dev.yanianz.star.calendar;
import org.bukkit.plugin.Plugin;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public final class EventScheduler {
    private final Plugin plugin;
    private final List<ScheduledEvent> scheduled = new ArrayList<>();

    public EventScheduler(@Nonnull Plugin plugin) { this.plugin = plugin; }

    public void schedule(@Nonnull String cron, @Nonnull Runnable task) {
        scheduled.add(new ScheduledEvent(cron, task));
    }

    public void checkAndRun() {
        for (ScheduledEvent se : new ArrayList<>(scheduled)) se.task.run();
    }

    private record ScheduledEvent(String cron, Runnable task) {}
}
