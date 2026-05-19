package dev.yanianz.star.commands.middleware;

import dev.yanianz.star.commands.CommandContext;

@FunctionalInterface
public interface CommandMiddleware {
    void intercept(CommandContext ctx, Runnable next);
}
