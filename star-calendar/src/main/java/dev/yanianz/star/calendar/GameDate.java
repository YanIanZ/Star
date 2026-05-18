package dev.yanianz.star.calendar;

public record GameDate(int day, int month, int year) implements Comparable<GameDate> {
    public static GameDate of(int month, int day) { return new GameDate(day, month, 0); }
    public static GameDate of(int month, int day, int year) { return new GameDate(day, month, year); }
    @Override public int compareTo(GameDate o) {
        if (year != o.year) return year - o.year;
        if (month != o.month) return month - o.month;
        return day - o.day;
    }
    public String toShortString() { return month + "/" + day; }
    @Override public String toString() { return year > 0 ? year + "-" + month + "-" + day : month + "/" + day; }
}
