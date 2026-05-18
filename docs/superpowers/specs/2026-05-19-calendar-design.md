# Design: star-calendar — Full Calendar Platform

**Date:** 2026-05-19 | **Project:** Star v1.6.0

## Classes (11)

| Class | Purpose |
|-------|---------|
| `GameDate.java` | In-game date (day, month, year) |
| `GameTime.java` | Date/time from world ticks + offset |
| `Calendar.java` | Named calendar, event CRUD |
| `CalendarManager.java` | Multi-calendar registry |
| `CalendarEvent.java` | Event: name, date, time, repeat, trigger |
| `EventTrigger.java` | `void fire(EventContext)` interface |
| `EventContext.java` | Context with players, world, date |
| `EventScheduler.java` | Cron engine + repeat handling |
| `EventHistory.java` | Execution log (event, time, status) |
| `RecurrenceRule.java` | Daily/weekly/monthly/yearly/cron |
| `CalendarView.java` | GUI calendar viewer using star-gui |

## API

```java
CalendarEvent.builder(name)
    .date(GameDate.of(month, day))
    .time("HH:mm")
    .recurring(RecurrenceRule.daily())
    .cron("0 20 * * 5")
    .trigger(ctx -> { ... })
    .build();
```

## Dependencies
- star-common, star-gui, Paper API
