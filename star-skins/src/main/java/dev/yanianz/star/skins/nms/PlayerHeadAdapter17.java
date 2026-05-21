package dev.yanianz.star.skins.nms;

import com.destroystokyo.paper.profile.ProfileProperty;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;

class PlayerHeadAdapter17 implements PlayerHeadAdapter {

    private static final String PROPERTY_KEY = "textures";

    @Override
    @ParametersAreNonnullByDefault
    public void setGameProfile(Block block, GameProfile profile, boolean sendBlockUpdate) {
        if (!(block.getState() instanceof Skull skull)) return;

        com.destroystokyo.paper.profile.PlayerProfile playerProfile = Bukkit.createProfile(profile.getId(), profile.getName());

        Collection<Property> textures = profile.getProperties().get(PROPERTY_KEY);
        if (textures != null && !textures.isEmpty()) {
            Property texture = textures.iterator().next();
            playerProfile.setProperty(new ProfileProperty(PROPERTY_KEY, texture.value(), texture.signature()));
        }
        skull.setPlayerProfile(playerProfile);

        if (sendBlockUpdate) {
            skull.update(true, false);
        }
    }
}
