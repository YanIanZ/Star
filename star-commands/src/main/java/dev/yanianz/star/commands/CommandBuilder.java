package dev.yanianz.star.commands;

import dev.yanianz.star.commands.condition.CommandCondition;
import dev.yanianz.star.commands.condition.CooldownCondition;
import dev.yanianz.star.commands.condition.PlayerOnlyCondition;
import dev.yanianz.star.commands.flag.Flag;
import dev.yanianz.star.commands.middleware.CommandMiddleware;
import org.bukkit.command.CommandSender;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Fluent builder for constructing command nodes.
 * Use via {@link CommandManager#builder(String)}.
 */
public final class CommandBuilder {
    private final String name;
    private final List<String> aliases = new ArrayList<>();
    private String permission;
    private String usage;
    private String description;
    private final List<CommandNode.ArgDef> args = new ArrayList<>();
    private final Map<String, CommandNode.TabCompleter> tabCompleters = new HashMap<>();
    private final List<CommandCondition> conditions = new ArrayList<>();
    private final List<CommandMiddleware> middleware = new ArrayList<>();
    private final List<Flag> flags = new ArrayList<>();
    private Consumer<CommandContext> executor;
    private CommandManager manager;
    private CooldownManager cooldownManager;

    public CommandBuilder(@Nonnull String name) {
        this.name = name;
    }

    void setManager(@Nonnull CommandManager manager) {
        this.manager = manager;
        this.cooldownManager = manager.cooldowns();
    }

    @Nonnull public CommandBuilder aliases(@Nonnull String... aliases) { Collections.addAll(this.aliases, aliases); return this; }
    @Nonnull public CommandBuilder permission(@Nonnull String perm) { this.permission = perm; return this; }
    @Nonnull public CommandBuilder usage(@Nonnull String usage) { this.usage = usage; return this; }
    @Nonnull public CommandBuilder description(@Nonnull String desc) { this.description = desc; return this; }

    @Nonnull public CommandBuilder arg(@Nonnull String name, @Nonnull ArgumentType<?> type) {
        args.add(new CommandNode.ArgDef(name, type, false)); return this;
    }

    @Nonnull public CommandBuilder optionalArg(@Nonnull String name, @Nonnull ArgumentType<?> type) {
        args.add(new CommandNode.ArgDef(name, type, true)); return this;
    }

    @Nonnull public CommandBuilder tabComplete(@Nonnull String argName, @Nonnull CommandNode.TabCompleter completer) {
        tabCompleters.put(argName, completer); return this;
    }

    @Nonnull public CommandBuilder executor(@Nonnull Consumer<CommandContext> executor) {
        this.executor = executor; return this;
    }

    @Nonnull public CommandBuilder condition(@Nonnull CommandCondition c) { conditions.add(c); return this; }
    @Nonnull public CommandBuilder playerOnly() { conditions.add(new PlayerOnlyCondition()); return this; }
    @Nonnull public CommandBuilder cooldown(int seconds) { if (cooldownManager != null) conditions.add(new CooldownCondition(seconds, cooldownManager)); return this; }
    @Nonnull public CommandBuilder middleware(@Nonnull CommandMiddleware m) { middleware.add(m); return this; }
    @Nonnull public CommandBuilder flag(@Nonnull String name, @Nonnull ArgumentType<?> type, @Nonnull String shortAlias) { flags.add(new Flag(name, type, shortAlias)); return this; }

    @Nonnull public CommandNode build() {
        if (executor == null) throw new IllegalStateException("executor is required");
        return new CommandNode(name, aliases, permission, usage, description, executor, args, tabCompleters, conditions, middleware, flags);
    }

    public void register() {
        if (manager == null) throw new IllegalStateException("builder not attached to CommandManager");
        manager.register(build());
    }
}
