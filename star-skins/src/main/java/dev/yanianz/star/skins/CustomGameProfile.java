package dev.yanianz.star.skins;

import java.net.URL;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public final class CustomGameProfile {

    private static final String PLAYER_NAME = "CS-CoreLib";
    private static final String PROPERTY_KEY = "textures";

    private final UUID uuid;
    private final URL skinUrl;
    private final String texture;

    CustomGameProfile(@Nonnull UUID uuid, @Nullable String texture, @Nonnull URL url) {
        this.uuid = uuid;
        this.skinUrl = url;
        this.texture = texture;
    }

    void apply(@Nonnull SkullMeta meta) {
        PlayerProfile playerProfile = Bukkit.createPlayerProfile(uuid, PLAYER_NAME);
        PlayerTextures playerTextures = playerProfile.getTextures();
        playerTextures.setSkin(skinUrl);
        playerProfile.setTextures(playerTextures);
        meta.setOwnerProfile(playerProfile);
    }

    @Nonnull
    public GameProfile getGameProfile() {
        GameProfile profile = new GameProfile(uuid, PLAYER_NAME);
        if (texture != null) {
            profile.getProperties().put(PROPERTY_KEY, new Property(PROPERTY_KEY, texture));
        }
        return profile;
    }

    @Nonnull
    public UUID getId() {
        return uuid;
    }

    @Nonnull
    public String getName() {
        return PLAYER_NAME;
    }

    @Nullable
    public String getBase64Texture() {
        return texture;
    }
}
