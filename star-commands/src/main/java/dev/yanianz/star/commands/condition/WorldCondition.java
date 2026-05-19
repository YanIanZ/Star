package dev.yanianz.star.commands.condition;

import dev.yanianz.star.commands.CommandContext;
import javax.annotation.Nonnull;

public record WorldCondition(@Nonnull String name) implements CommandCondition {
    @Override
    public boolean test(CommandContext ctx) {
        return ctx.isPlayer() && ctx.asPlayer().getWorld().getName().equalsIgnoreCase(name);
    }

    @Override
    public String getFailureMessage() {
        return "Wrong world.";
    }
}
