package dev.yanianz.star.commands.condition;

import dev.yanianz.star.commands.CommandContext;
import javax.annotation.Nonnull;
import java.util.function.Predicate;

public record CustomCondition(@Nonnull Predicate<CommandContext> predicate, @Nonnull String message) implements CommandCondition {
    @Override
    public boolean test(CommandContext ctx) {
        return predicate.test(ctx);
    }

    @Override
    public String getFailureMessage() {
        return message;
    }
}
