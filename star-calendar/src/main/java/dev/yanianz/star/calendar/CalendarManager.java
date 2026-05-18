package dev.yanianz.star.calendar;
import dev.yanianz.star.common.StarLogger;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public final class CalendarManager {
    private final Plugin plugin;
    private final StarLogger logger;
    private final Map<String, Calendar> calendars = new ConcurrentHashMap<>();
    private GameDate lastChecked;

    public CalendarManager(@Nonnull Plugin plugin) {
        this.plugin = plugin; this.logger = new StarLogger(plugin.getServer(), "calendar");
    }

    @Nonnull public Calendar create(@Nonnull String name) {
        Calendar cal = new Calendar(name); calendars.put(name, cal);
        logger.log(Level.INFO, "Created calendar: " + name); return cal;
    }

    @Nonnull public Optional<Calendar> get(@Nonnull String name) { return Optional.ofNullable(calendars.get(name)); }
    public void delete(@Nonnull String name) { calendars.remove(name); }
    @Nonnull public Collection<Calendar> getAll() { return calendars.values(); }

    public void startTicking(@Nonnull World world) {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            GameDate today = GameTime.now(world);
            if (!today.equals(lastChecked)) {
                lastChecked = today;
                Collection<Player> players = (Collection<Player>) world.getPlayers();
                EventContext ctx = new EventContext(world, today, players);
                for (Calendar cal : calendars.values()) cal.fire(today, ctx);
            }
        }, 0, 1200);
    }
}
