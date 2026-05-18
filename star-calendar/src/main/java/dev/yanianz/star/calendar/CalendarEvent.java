package dev.yanianz.star.calendar;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class CalendarEvent {
    private final String name, cronExpression, time;
    private final GameDate date;
    private final RecurrenceRule recurrence;
    private final EventTrigger trigger;

    CalendarEvent(String name, GameDate date, String time, RecurrenceRule recurrence, String cronExpression, EventTrigger trigger) {
        this.name = name; this.date = date; this.time = time; this.recurrence = recurrence; this.cronExpression = cronExpression; this.trigger = trigger;
    }

    @Nonnull public String getName() { return name; }
    @Nullable public GameDate getDate() { return date; }
    @Nullable public String getTime() { return time; }
    @Nonnull public RecurrenceRule getRecurrence() { return recurrence; }
    @Nullable public String getCronExpression() { return cronExpression; }
    @Nonnull public EventTrigger getTrigger() { return trigger; }

    public boolean matches(@Nonnull GameDate today) {
        if (recurrence == RecurrenceRule.DAILY) return true;
        if (date == null) return false;
        return switch (recurrence) {
            case WEEKLY -> date.day() == today.day();
            case MONTHLY -> date.month() == today.month() && date.day() == today.day();
            case YEARLY -> date.month() == today.month() && date.day() == today.day();
            case ONCE -> date.equals(today);
            default -> false;
        };
    }

    public void fire(@Nonnull EventContext ctx) { if (trigger != null) trigger.fire(ctx); }

    @Nonnull public static Builder builder(@Nonnull String name) { return new Builder(name); }

    public static final class Builder {
        private final String name;
        private GameDate date;
        private String time, cronExpression;
        private RecurrenceRule recurrence = RecurrenceRule.ONCE;
        private EventTrigger trigger;

        Builder(String name) { this.name = name; }

        @Nonnull public Builder date(@Nonnull GameDate d) { this.date = d; return this; }
        @Nonnull public Builder time(@Nonnull String t) { this.time = t; return this; }
        @Nonnull public Builder recurring(@Nonnull RecurrenceRule r) { this.recurrence = r; return this; }
        @Nonnull public Builder cron(@Nonnull String c) { this.cronExpression = c; this.recurrence = RecurrenceRule.CRON; return this; }
        @Nonnull public Builder trigger(@Nonnull EventTrigger t) { this.trigger = t; return this; }

        @Nonnull public CalendarEvent build() { return new CalendarEvent(name, date, time, recurrence, cronExpression, trigger); }
    }
}
