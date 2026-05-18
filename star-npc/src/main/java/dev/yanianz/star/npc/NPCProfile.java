package dev.yanianz.star.npc;
import javax.annotation.Nonnull;
import java.util.UUID;

public record NPCProfile(@Nonnull String name, @Nonnull UUID uuid, @Nonnull String skin) {
    @Nonnull public static NPCProfile of(@Nonnull String name, @Nonnull String skinBase64) {
        return new NPCProfile(name, UUID.randomUUID(), skinBase64);
    }
}
