package dev.yanianz.star.calendar;
import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class Calendar {
    private final String name;
    private final List<CalendarEvent> events = new CopyOnWriteArrayList<>();
    private final EventHistory history = new EventHistory();

    public Calendar(@Nonnull String name) { this.name = name; }

    @Nonnull public String getName() { return name; }

    public void addEvent(@Nonnull CalendarEvent event) { events.add(event); }
    public boolean removeEvent(@Nonnull String name) { return events.removeIf(e -> e.getName().equals(name)); }
    public void clearEvents() { events.clear(); }

    @Nonnull public List<CalendarEvent> getEvents(@Nonnull GameDate date) {
        return events.stream().filter(e -> e.matches(date)).toList();
    }

    @Nonnull public List<CalendarEvent> getAllEvents() { return List.copyOf(events); }
    @Nonnull public EventHistory getHistory() { return history; }

    public void fire(@Nonnull GameDate date, @Nonnull EventContext ctx) {
        for (CalendarEvent event : getEvents(date)) {
            try { event.fire(ctx); history.record(event.getName(), date, true); }
            catch (Exception e) { history.record(event.getName(), date, false); }
        }
    }
}
