package dev.yanianz.star.commands;

import dev.yanianz.star.common.StarLogger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;
import java.util.function.Consumer;

/**
 * Central command registration manager.
 * Supports two registration styles: annotation-based ({@link #register(Object)})
 * and builder-based ({@link #builder(String)}).
 */
public final class CommandManager {
    private final Plugin plugin;
    private final StarLogger logger;
    private final Map<String, CompositeCommand> composites = new HashMap<>();
    private final CooldownManager cooldowns = new CooldownManager();

    public CommandManager(@Nonnull Plugin plugin) {
        this.plugin = plugin;
        this.logger = new StarLogger(plugin.getServer(), "commands");
    }

    @Nonnull public CooldownManager cooldowns() { return cooldowns; }

    /**
     * Creates a new {@link CommandBuilder} for constructing a command fluently.
     */
    @Nonnull public CommandBuilder builder(@Nonnull String name) {
        CommandBuilder b = new CommandBuilder(name);
        b.setManager(this);
        return b;
    }

    public void register(@Nonnull CommandNode node) {
        CompositeCommand cc = composites.computeIfAbsent(node.getName(), n -> new CompositeCommand(plugin, n));
        cc.addNode(node);
        CommandExecutor executor = cc::execute;
        TabCompleter completer = cc::tabComplete;
        PluginCommand cmd = plugin.getServer().getPluginCommand(node.getName());
        if (cmd == null) {
            PluginCommand pc = Bukkit.getPluginCommand(node.getName());
            if (pc != null) {
                pc.setExecutor(executor);
                pc.setTabCompleter(completer);
            } else {
                logger.log(Level.WARNING, "Cannot find command '" + node.getName() + "'. Is it declared in plugin.yml?");
            }
        } else {
            cmd.setExecutor(executor);
            cmd.setTabCompleter(completer);
        }
    }

    /**
     * Registers a command object by scanning its {@link Command}, {@link Arg},
     * and {@link TabComplete} annotations.
     */
    public void register(@Nonnull Object obj) {
        Class<?> clazz = obj.getClass();
        Command typeAnnotation = clazz.getAnnotation(Command.class);
        String parentName = typeAnnotation != null && !typeAnnotation.name().isEmpty() ? typeAnnotation.name() : null;

        for (Method method : clazz.getDeclaredMethods()) {
            Command cmd = method.getAnnotation(Command.class);
            if (cmd == null) continue;

            String cmdName = cmd.name().isEmpty() && parentName != null ? parentName : cmd.name();
            if (cmdName.isEmpty()) {
                logger.log(Level.WARNING, "Command method " + method.getName() + " has no name");
                continue;
            }

            List<String> aliases = new ArrayList<>();
            Collections.addAll(aliases, cmd.aliases());

            Consumer<CommandContext> executor = ctx -> {
                try {
                    java.lang.reflect.Parameter[] params = method.getParameters();
                    Object[] methodArgs = new Object[params.length];
                    for (int i = 0; i < params.length; i++) {
                        if (params[i].getType().equals(CommandContext.class)) {
                            methodArgs[i] = ctx;
                        } else {
                            Arg arg = params[i].getAnnotation(Arg.class);
                            if (arg != null) {
                                methodArgs[i] = ctx.get(arg.value());
                            }
                        }
                    }
                    method.invoke(obj, methodArgs);
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Error executing command " + cmdName, e);
                }
            };

            List<CommandNode.ArgDef> argDefs = new ArrayList<>();
            for (java.lang.reflect.Parameter p : method.getParameters()) {
                Arg arg = p.getAnnotation(Arg.class);
                if (arg != null) {
                    ArgumentType<?> type = TYPE_MAP.getOrDefault(p.getType(), ArgumentType.STRING);
                    argDefs.add(new CommandNode.ArgDef(arg.value(), type, arg.optional()));
                }
            }

            Map<String, CommandNode.TabCompleter> tabCompleters = new HashMap<>();
            TabComplete tab = method.getAnnotation(TabComplete.class);
            if (tab != null && tab.suggestions().length > 0 && !argDefs.isEmpty()) {
                List<String> suggs = List.of(tab.suggestions());
                tabCompleters.put(argDefs.get(0).name(), ctx -> suggs);
            }

            CommandNode node = new CommandNode(cmdName, aliases,
                cmd.permission().isEmpty() ? null : cmd.permission(),
                cmd.usage().isEmpty() ? null : cmd.usage(),
                cmd.description().isEmpty() ? null : cmd.description(),
                executor, argDefs, tabCompleters);

            register(node);
        }
    }

    private static final Map<Class<?>, ArgumentType<?>> TYPE_MAP = Map.ofEntries(
        Map.entry(int.class, ArgumentType.INTEGER),
        Map.entry(Integer.class, ArgumentType.INTEGER),
        Map.entry(double.class, ArgumentType.DOUBLE),
        Map.entry(Double.class, ArgumentType.DOUBLE),
        Map.entry(boolean.class, ArgumentType.BOOLEAN),
        Map.entry(Boolean.class, ArgumentType.BOOLEAN),
        Map.entry(org.bukkit.entity.Player.class, ArgumentType.PLAYER)
    );

    public void scan(@Nonnull String packageName) {
        logger.log(Level.INFO, "Package scanning not available; use register(Object) instead");
    }
}
