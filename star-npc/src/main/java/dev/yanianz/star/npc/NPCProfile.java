package dev.yanianz.star.npc;

import javax.annotation.Nonnull;
import java.util.UUID;

/** NPC identity: name, skin, UUID. */
public final class NPCProfile {
    private final String name;
    private final UUID uuid;
    private final String skin;

    public NPCProfile(@Nonnull String name, @Nonnull String skinBase64) {
        this.name = name;
        this.uuid = UUID.randomUUID();
        this.skin = skinBase64;
    }

    @Nonnull
    public static NPCProfile of(@Nonnull String name, @Nonnull String skinBase64) {
        return new NPCProfile(name, skinBase64);
    }

    @Nonnull
    public String getName() { return name; }

    @Nonnull
    public UUID getUuid() { return uuid; }

    @Nonnull
    public String getSkin() { return skin; }
}
