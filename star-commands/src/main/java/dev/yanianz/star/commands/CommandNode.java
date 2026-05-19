package dev.yanianz.star.commands;

import dev.yanianz.star.commands.condition.CommandCondition;
import dev.yanianz.star.commands.flag.Flag;
import dev.yanianz.star.commands.middleware.CommandMiddleware;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class CommandNode {
    private final String name;
    private final List<String> aliases;
    private final String permission;
    private final String usage;
    private final String description;
    private final Consumer<CommandContext> executor;
    private final List<ArgDef> args;
    private final Map<String, TabCompleter> tabCompleters;
    private final List<CommandCondition> conditions;
    private final List<CommandMiddleware> middleware;
    private final List<Flag> flags;

    public record ArgDef(@Nonnull String name, @Nonnull ArgumentType<?> type, boolean optional) {
        @Nonnull public ArgDef withType(@Nonnull ArgumentType<?> type) { return new ArgDef(name, type, optional); }
    }
    public interface TabCompleter { @Nonnull List<String> complete(@Nonnull CommandContext ctx); }

    public CommandNode(@Nonnull String name, @Nonnull List<String> aliases, @Nullable String permission,
                       @Nullable String usage, @Nullable String description,
                       @Nonnull Consumer<CommandContext> executor,
                       @Nonnull List<ArgDef> args,
                       @Nonnull Map<String, TabCompleter> tabCompleters) {
        this(name, aliases, permission, usage, description, executor, args, tabCompleters,
            Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    }

    public CommandNode(@Nonnull String name, @Nonnull List<String> aliases, @Nullable String permission,
                       @Nullable String usage, @Nullable String description,
                       @Nonnull Consumer<CommandContext> executor,
                       @Nonnull List<ArgDef> args,
                       @Nonnull Map<String, TabCompleter> tabCompleters,
                       @Nonnull List<CommandCondition> conditions,
                       @Nonnull List<CommandMiddleware> middleware,
                       @Nonnull List<Flag> flags) {
        this.name = name;
        this.aliases = aliases;
        this.permission = permission;
        this.usage = usage;
        this.description = description;
        this.executor = executor;
        this.args = args;
        this.tabCompleters = tabCompleters;
        this.conditions = conditions;
        this.middleware = middleware;
        this.flags = flags;
    }

    @Nonnull
    public String getName() { return name; }

    @Nonnull
    public List<String> getAliases() { return aliases; }

    @Nullable
    public String getPermission() { return permission; }

    @Nullable
    public String getUsage() { return usage; }

    @Nullable
    public String getDescription() { return description; }

    @Nonnull
    public Consumer<CommandContext> getExecutor() { return executor; }

    @Nonnull
    public List<ArgDef> getArgs() { return args; }

    @Nonnull
    public Map<String, TabCompleter> getTabCompleters() { return tabCompleters; }

    @Nonnull
    public List<CommandCondition> getConditions() { return conditions; }

    @Nonnull
    public List<CommandMiddleware> getMiddleware() { return middleware; }

    @Nonnull
    public List<Flag> getFlags() { return flags; }

    public boolean evaluateConditions(CommandContext ctx) {
        for (CommandCondition c : conditions) {
            if (!c.test(ctx)) {
                ctx.send(c.getFailureMessage());
                return false;
            }
        }
        return true;
    }

    public void executeWithMiddleware(CommandContext ctx) {
        if (!evaluateConditions(ctx)) return;
        Runnable chain = () -> executor.accept(ctx);
        for (int i = middleware.size() - 1; i >= 0; i--) {
            CommandMiddleware m = middleware.get(i);
            Runnable next = chain;
            chain = () -> m.intercept(ctx, next);
        }
        chain.run();
    }
}
