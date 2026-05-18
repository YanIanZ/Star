package dev.yanianz.star.calendar;
import org.bukkit.World;
import javax.annotation.Nonnull;

public final class GameTime {
    private GameTime() {}
    public static GameDate now(@Nonnull World world) { return fromTicks(world.getFullTime()); }
    public static GameDate fromTicks(long ticks) { long days = ticks / 24000; long year = days / 365 + 1; long dayOfYear = days % 365; long month = (dayOfYear / 30) + 1; long day = (dayOfYear % 30) + 1; return GameDate.of((int) month, (int) day, (int) year); }
}
