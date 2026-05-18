package dev.yanianz.star.calendar;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

@DisplayName("Calendar")
class TestCalendar {
    @Test @DisplayName("GameDate comparison")
    void gameDateCompare() {
        GameDate a = GameDate.of(1, 5);
        GameDate b = GameDate.of(6, 10);
        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);
        assertEquals(0, GameDate.of(3, 15).compareTo(GameDate.of(3, 15)));
    }

    @Test @DisplayName("CalendarEvent matches date")
    void eventMatches() {
        CalendarEvent evt = CalendarEvent.builder("Test").date(GameDate.of(6, 15)).recurring(RecurrenceRule.MONTHLY).build();
        assertTrue(evt.matches(GameDate.of(6, 15)));
        assertFalse(evt.matches(GameDate.of(6, 16)));
    }

    @Test @DisplayName("Daily event matches any date")
    void dailyEvent() {
        CalendarEvent evt = CalendarEvent.builder("Daily").recurring(RecurrenceRule.DAILY).build();
        assertTrue(evt.matches(GameDate.of(1, 1)));
        assertTrue(evt.matches(GameDate.of(12, 31)));
    }

    @Test @DisplayName("Calendar add and fire events")
    void calendarFire() {
        Calendar cal = new Calendar("test");
        boolean[] fired = {false};
        cal.addEvent(CalendarEvent.builder("Test").date(GameDate.of(5, 10)).recurring(RecurrenceRule.MONTHLY).trigger(ctx -> fired[0] = true).build());
        cal.fire(GameDate.of(5, 10), null);
        assertTrue(fired[0]);
        assertEquals(1, cal.getHistory().getEntries().size());
    }

    @Test @DisplayName("GameTime from ticks")
    void gameTime() {
        GameDate date = GameTime.fromTicks(24000);
        assertEquals(2, date.day());
        assertEquals(1, date.month());
        assertEquals(1, date.year());
    }

    @Test @DisplayName("CalendarManager create and get")
    void calendarManager() {
        Calendar cal = new Calendar("events");
        assertEquals("events", cal.getName());
        cal.addEvent(CalendarEvent.builder("evt").recurring(RecurrenceRule.DAILY).build());
        assertEquals(1, cal.getAllEvents().size());
    }
}
