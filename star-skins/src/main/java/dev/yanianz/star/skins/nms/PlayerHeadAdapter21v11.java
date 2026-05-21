package dev.yanianz.star.skins.nms;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import javax.annotation.ParametersAreNonnullByDefault;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Base64;
import java.util.Collection;

class PlayerHeadAdapter21v11 implements PlayerHeadAdapter {

    private static final String PROPERTY_KEY = "textures";

    @Override
    @ParametersAreNonnullByDefault
    public void setGameProfile(Block block, GameProfile profile, boolean sendBlockUpdate) {
        if (!(block.getState() instanceof Skull skull)) return;

        Collection<Property> properties = profile.getProperties().get(PROPERTY_KEY);
        Property texture = (properties != null && !properties.isEmpty())
            ? properties.iterator().next()
            : null;

        String url = null;
        if (texture != null) {
            String json = new String(Base64.getDecoder().decode(texture.value()));
            int urlStart = json.indexOf("\"url\":\"") + 7;
            int urlEnd = json.indexOf("\"", urlStart);
            url = json.substring(urlStart, urlEnd);
        }

        PlayerProfile playerProfile = Bukkit.createPlayerProfile(profile.getId(), profile.getName());
        if (url != null) {
            try {
                PlayerTextures pt = playerProfile.getTextures();
                pt.setSkin(URI.create(url).toURL());
                playerProfile.setTextures(pt);
            } catch (MalformedURLException ignored) {}
        }
        skull.setOwnerProfile(playerProfile);

        if (sendBlockUpdate) {
            skull.update(true, false);
        }
    }
}
