package dev.yanianz.star.calendar;
@FunctionalInterface
public interface EventTrigger { void fire(EventContext ctx); }
