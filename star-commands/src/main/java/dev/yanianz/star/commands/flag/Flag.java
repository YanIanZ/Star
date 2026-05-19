package dev.yanianz.star.commands.flag;

import dev.yanianz.star.commands.ArgumentType;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public record Flag(@Nonnull String name, @Nonnull String shortAlias, @Nonnull ArgumentType<?> type, @Nullable Object defaultValue, @Nonnull String description) {
    public Flag(@Nonnull String name, @Nonnull ArgumentType<?> type, @Nonnull String shortAlias) {
        this(name, shortAlias, type, null, "");
    }
}
