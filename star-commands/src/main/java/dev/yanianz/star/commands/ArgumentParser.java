package dev.yanianz.star.commands;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses raw string arguments into typed values based on {@link CommandNode.ArgDef} definitions.
 * Also provides tab completion for argument types.
 */
public final class ArgumentParser {
    private ArgumentParser() {}

    @Nonnull
    public static Map<String, Object> parse(@Nonnull CommandContext ctx, @Nonnull List<CommandNode.ArgDef> args) {
        Map<String, Object> result = new LinkedHashMap<>();
        String[] raw = ctx.args();
        int i = 0;
        for (CommandNode.ArgDef def : args) {
            if (i < raw.length) {
                Object value = def.type().parse(raw[i]);
                if (value != null) {
                    result.put(def.name(), value);
                    i++;
                } else if (!def.optional()) {
                    throw new IllegalArgumentException("Invalid value for argument '" + def.name() + "': " + raw[i]);
                }
            } else if (!def.optional()) {
                throw new IllegalArgumentException("Missing required argument: " + def.name());
            }
        }
        return result;
    }

    @Nonnull
    public static List<String> tabComplete(@Nonnull CommandContext ctx, @Nonnull List<CommandNode.ArgDef> args,
                                            @Nonnull Map<String, CommandNode.TabCompleter> completers) {
        List<String> results = new ArrayList<>();
        String[] raw = ctx.args();
        int targetIndex = Math.min(raw.length - 1, args.size() - 1);
        if (targetIndex < 0) return results;
        CommandNode.ArgDef def = args.get(targetIndex);
        CommandNode.TabCompleter custom = completers.get(def.name());
        if (custom != null) {
            results.addAll(custom.complete(ctx));
        }
        if (results.isEmpty()) {
            results.addAll(def.type().tabComplete());
        }
        String prefix = raw.length > 0 ? raw[raw.length - 1].toLowerCase() : "";
        return results.stream().filter(s -> s.toLowerCase().startsWith(prefix)).toList();
    }
}
