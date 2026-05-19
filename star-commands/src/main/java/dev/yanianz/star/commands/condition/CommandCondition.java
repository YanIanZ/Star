package dev.yanianz.star.commands.condition;

import dev.yanianz.star.commands.CommandContext;

@FunctionalInterface
public interface CommandCondition {
    boolean test(CommandContext ctx);

    default String getFailureMessage() {
        return "You cannot use this command.";
    }
}
