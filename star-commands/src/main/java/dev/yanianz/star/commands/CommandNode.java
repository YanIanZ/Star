package dev.yanianz.star.commands;

import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    public record ArgDef(@Nonnull String name, @Nonnull ArgumentType<?> type, boolean optional) {
        @Nonnull public ArgDef withType(@Nonnull ArgumentType<?> type) { return new ArgDef(name, type, optional); }
    }
    public interface TabCompleter { @Nonnull List<String> complete(@Nonnull CommandContext ctx); }

    public CommandNode(@Nonnull String name, @Nonnull List<String> aliases, @Nullable String permission,
                       @Nullable String usage, @Nullable String description,
                       @Nonnull Consumer<CommandContext> executor,
                       @Nonnull List<ArgDef> args,
                       @Nonnull Map<String, TabCompleter> tabCompleters) {
        this.name = name;
        this.aliases = aliases;
        this.permission = permission;
        this.usage = usage;
        this.description = description;
        this.executor = executor;
        this.args = args;
        this.tabCompleters = tabCompleters;
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
}
