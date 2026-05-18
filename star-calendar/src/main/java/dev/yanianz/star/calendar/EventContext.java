package dev.yanianz.star.calendar;
import org.bukkit.World;
import org.bukkit.entity.Player;
import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;

public final class EventContext {
    private final World world;
    private final GameDate date;
    private final Collection<Player> players;
    public EventContext(World world, GameDate date, Collection<Player> players) { this.world = world; this.date = date; this.players = players; }
    @Nonnull public World world() { return world; }
    @Nonnull public GameDate date() { return date; }
    @Nonnull public Collection<Player> players() { return players != null ? players : Collections.emptyList(); }
}
