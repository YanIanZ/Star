package dev.yanianz.star.misc;

import javax.annotation.Nonnull;
import java.util.Map;

public final class PlaceholderResolver {
    private PlaceholderResolver() {}

    @Nonnull
    public static String resolve(@Nonnull String template, @Nonnull Map<String, String> placeholders) {
        String result = template;
        for (Map.Entry<String, String> e : placeholders.entrySet())
            result = result.replace("{" + e.getKey() + "}", e.getValue());
        return result;
    }
}
