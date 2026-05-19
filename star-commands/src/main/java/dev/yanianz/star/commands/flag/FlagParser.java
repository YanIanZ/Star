package dev.yanianz.star.commands.flag;

import dev.yanianz.star.commands.CommandContext;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public final class FlagParser {
    private FlagParser() {}

    @Nonnull
    public static Map<String, Object> parse(@Nonnull CommandContext ctx, @Nonnull List<Flag> flags) {
        Map<String, Object> result = new HashMap<>();
        String[] args = ctx.args();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (!arg.startsWith("-")) continue;
            String name = arg.startsWith("--") ? arg.substring(2) : arg.substring(1);
            for (Flag flag : flags) {
                if (flag.name().equals(name) || flag.shortAlias().equals(name)) {
                    if (i + 1 < args.length) {
                        Object val = flag.type().parse(args[i + 1]);
                        if (val != null) result.put(flag.name(), val);
                    }
                    i++;
                    break;
                }
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> T get(@Nonnull Map<String, Object> parsed, @Nonnull String flag, @Nullable T defaultValue) {
        return (T) parsed.getOrDefault(flag, defaultValue);
    }
}
