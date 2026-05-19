package dev.yanianz.star.commands.help;

import dev.yanianz.star.commands.CommandNode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import javax.annotation.Nonnull;
import java.util.*;

public final class HelpGenerator {
    private HelpGenerator() {}

    @Nonnull
    public static Component generate(@Nonnull Collection<CommandNode> nodes, @Nonnull String commandName) {
        var builder = Component.text()
            .append(Component.text("=== " + commandName + " Help ===\n", NamedTextColor.GOLD));
        for (CommandNode node : nodes) {
            builder.append(Component.text("  /" + commandName + " " + node.getName(), NamedTextColor.YELLOW));
            if (node.getDescription() != null) {
                builder.append(Component.text(" - " + node.getDescription(), NamedTextColor.GRAY));
            }
            builder.append(Component.text("\n"));
        }
        return builder.build();
    }
}
