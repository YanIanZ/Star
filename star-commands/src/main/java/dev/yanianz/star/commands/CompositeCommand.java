package dev.yanianz.star.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Groups multiple {@link CommandNode}s under a single root command.
 * Handles subcommand routing, permission checks, and argument parsing.
 */
public final class CompositeCommand {
    private final Plugin plugin;
    private final String name;
    private final List<CommandNode> nodes = new ArrayList<>();
    private CommandNode defaultNode;

    public CompositeCommand(@Nonnull Plugin plugin, @Nonnull String name) {
        this.plugin = plugin; this.name = name;
    }

    @Nonnull public Plugin getPlugin() { return plugin; }
    @Nonnull public String getName() { return name; }

    public void addNode(@Nonnull CommandNode node) {
        nodes.add(node);
        if (defaultNode == null) defaultNode = node;
    }

    @Nonnull public List<CommandNode> getNodes() { return List.copyOf(nodes); }

    @Nonnull
    public CommandNode matchNode(@Nonnull String label) {
        for (CommandNode node : nodes) {
            if (node.getName().equalsIgnoreCase(label)) return node;
            for (String alias : node.getAliases()) {
                if (alias.equalsIgnoreCase(label)) return node;
            }
        }
        return defaultNode;
    }

    public boolean execute(CommandSender sender, org.bukkit.command.Command cmd, String alias, String[] args) {
        String subCommand = args.length > 0 ? args[0] : "";
        String[] subArgs = args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[0];
        CommandNode matched = matchNode(subCommand);
        if (matched == null) return false;

        if (matched.getPermission() != null && !matched.getPermission().isEmpty() && !sender.hasPermission(matched.getPermission())) {
            sender.sendMessage("You don't have permission!");
            return true;
        }

        CommandContext ctx = new CommandContext(sender, subCommand.isEmpty() ? alias : subCommand, subArgs, matched.getFlags());
        try {
            Map<String, Object> parsed = ArgumentParser.parse(ctx, matched.getArgs());
            for (Map.Entry<String, Object> e : parsed.entrySet()) ctx.set(e.getKey(), e.getValue());
            matched.executeWithMiddleware(ctx);
        } catch (IllegalArgumentException e) {
            if (matched.getUsage() != null) ctx.send("Usage: " + matched.getUsage());
            else ctx.send(e.getMessage());
        }
        return true;
    }

    public List<String> tabComplete(CommandSender sender, org.bukkit.command.Command cmd, String alias, String[] args) {
        CommandNode matched;
        if (args.length <= 1) {
            List<String> subCommands = new ArrayList<>();
            for (CommandNode n : nodes) {
                subCommands.add(n.getName());
                subCommands.addAll(n.getAliases());
            }
            String prefix = args.length > 0 ? args[0].toLowerCase() : "";
            return subCommands.stream().filter(s -> s.toLowerCase().startsWith(prefix)).toList();
        }

        matched = matchNode(args[0]);
        if (matched == null) return Collections.emptyList();

        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        CommandContext ctx = new CommandContext(sender, args[0], subArgs);
        return ArgumentParser.tabComplete(ctx, matched.getArgs(), matched.getTabCompleters());
    }
}
