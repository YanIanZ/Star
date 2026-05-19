package dev.yanianz.star.commands.condition;

import dev.yanianz.star.commands.CommandContext;

public record PlayerOnlyCondition() implements CommandCondition {
    @Override
    public boolean test(CommandContext ctx) {
        return ctx.isPlayer();
    }

    @Override
    public String getFailureMessage() {
        return "Player only.";
    }
}
