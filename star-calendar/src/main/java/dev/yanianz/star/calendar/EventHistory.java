package dev.yanianz.star.calendar;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public final class EventHistory {
    private final List<Entry> entries = new CopyOnWriteArrayList<>();
    public void record(String event, GameDate date, boolean success) { entries.add(new Entry(event, date, success)); }
    public List<Entry> getEntries() { return List.copyOf(entries); }
    public void clear() { entries.clear(); }
    public record Entry(String eventName, GameDate date, boolean success) {}
}
