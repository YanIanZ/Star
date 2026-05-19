package dev.yanianz.star.commands.condition;

import dev.yanianz.star.commands.CommandContext;
import javax.annotation.Nonnull;

public record PermissionCondition(@Nonnull String permission) implements CommandCondition {
    @Override
    public boolean test(CommandContext ctx) {
        return ctx.sender().hasPermission(permission);
    }

    @Override
    public String getFailureMessage() {
        return "No permission.";
    }
}
