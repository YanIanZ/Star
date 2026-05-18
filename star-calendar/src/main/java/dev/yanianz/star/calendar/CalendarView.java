package dev.yanianz.star.calendar;
import dev.yanianz.star.gui.GuiBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import javax.annotation.Nonnull;
import java.util.List;

public final class CalendarView {
    private final Calendar calendar;
    private final GameDate date;

    public CalendarView(@Nonnull Calendar calendar, @Nonnull GameDate date) {
        this.calendar = calendar; this.date = date;
    }

    public void open(@Nonnull Player player, @Nonnull Plugin plugin) {
        List<CalendarEvent> events = calendar.getEvents(date);
        GuiBuilder.create(3, Component.text(calendar.getName() + " - " + date.toShortString()))
            .slot(4, new ItemStack(Material.CLOCK), e -> {})
            .slot(13, new ItemStack(Material.PAPER), e -> player.sendMessage("Events: " + events.size()))
            .fill(new ItemStack(Material.GRAY_STAINED_GLASS_PANE))
            .build().open(player, plugin);
    }
}
