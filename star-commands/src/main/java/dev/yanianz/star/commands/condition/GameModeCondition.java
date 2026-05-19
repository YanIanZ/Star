package dev.yanianz.star.commands.condition;

import dev.yanianz.star.commands.CommandContext;
import org.bukkit.GameMode;
import javax.annotation.Nonnull;

public record GameModeCondition(@Nonnull GameMode mode) implements CommandCondition {
    @Override
    public boolean test(CommandContext ctx) {
        return ctx.isPlayer() && ctx.asPlayer().getGameMode() == mode;
    }

    @Override
    public String getFailureMessage() {
        return "Must be in " + mode.name() + ".";
    }
}
